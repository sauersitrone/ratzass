# TurboPi (Hiwonder) – Java Equivalent of the Python/ROS 2 Course

Reference for porting the software functions of the Hiwonder **TurboPi** (Raspberry Pi 5, mecanum chassis,
pan-tilt camera, optional 2-DOF arm + gripper, WonderEcho Pro voice module) from Python/ROS 2 to **Java 21**.

Source: Hiwonder wiki, "TurboPi – Advanced", chapters 1–9
(`https://wiki.hiwonder.com/projects/TurboPi/en/advanced/docs/…`), read on 2026-11-27.

---

## 0. Read this first – what the source does and does not contain

| Chapter | Status | Note |
|---|---|---|
| 1 Read first | read | Hardware, ports, servo IDs, Docker, login |
| 2 Quick experience | read | WonderPi app modes (no algorithms/parameters given) |
| 3 Mecanum control | read (partly summary) | `cmd_vel` demos, arm topics; **rotation formula is an image and did not load** |
| 4 ROS + OpenCV | read (middle part summary only) | **4.6 QR code not readable** |
| 5 MediaPipe | **NOT available – page returns HTTP 404** (also the sidebar link) | Only the section titles are known |
| 6 Machine learning | read (second half summary) | YOLO26 training workflow; **no inference code on the page** |
| 7 Network | read | Config in images, keys unknown |
| 8 Large AI models | read (large middle part summary) | OpenAI/OpenRouter/DashScope, WonderEcho, ROS nodes |
| 9 OpenClaw | read | Agent gateway on a VM + ROS 2 services on the robot |

Markers used below:

- **[wiki]** – stated on the wiki page.
- **[wiki-summary]** – from a part of a page that was only available as a summary; verify.
- **[verify]** – not on the wiki; comes from general knowledge of the Hiwonder SDK / ROS / libraries.
  Check it against the source files on the robot before relying on it.

The wiki shows *how to use* the demos, not the full source code. The real Python sources are on the robot
inside the Docker container `TurboPi` (`/home/ubuntu/ros2_ws/src/...`, `/home/ubuntu/large_models/...`,
`/home/ubuntu/software/...`). **Copy them off the robot first** – they are the real spec for the Java port:

```bash
docker exec -it -u ubuntu -w /home/ubuntu TurboPi /bin/bash
tar czf /tmp/turbopi-src.tgz ros2_ws/src large_models software third_party_ros2/yolo/*.py
# on the Pi host:
docker cp TurboPi:/tmp/turbopi-src.tgz .
```

---

## 1. Architecture choice for Java

Everything on the robot is ROS 2 (Python `rclpy` nodes) in Docker. There are two realistic ways to port it.

### Option A – Java app on top of the existing ROS 2 drivers (recommended start)

```
┌──────────── Java 21 app (your code) ─────────────┐
│ vision (OpenCV Java) · PID · state machines      │
│ LLM / ASR / TTS (openai-java) · YOLO (ONNX RT)   │
└───────────────┬──────────────────────────────────┘
                │ jros2 (IHMC) – native ROS 2 over DDS, topics
┌───────────────▼──────────────────────────────────┐
│ Hiwonder ROS 2 nodes (keep):                     │
│ controller (motors, servos, RGB, buzzer),        │
│ sonar_controller, usb_cam, …                      │
└───────────────┬──────────────────────────────────┘
                │ UART / I2C
            STM32 controller board, sonar, line sensor
```

You re-implement all *application logic* (chapter 3–9 demos) in Java and keep Hiwonder's low-level driver nodes.
Low risk, the hardware protocol does not need to be known.

**ROS 2 library: [jros2](https://github.com/ihmcrobotics/jros2)** (IHMC Robotics, Apache-2.0). Java API on top of a
bundled native Fast-DDS 3.6.2 (JNI); no ROS installation needed on the Java side; wire-compatible with
ROS 2 Foxy, Humble, Jazzy, Kilted; Linux x86_64/arm64/armhf (Raspberry Pi), Windows, macOS, Android.

**jros2 currently supports topics only** – services, actions and parameters are marked "soon" in its README.
This shapes the design (details in §4.3):

- The Hiwonder *driver* nodes are driven entirely through topics (`/cmd_vel`, `set_rgb`, `set_buzzer`, servo
  topics, sonar distance, `/image_raw`) → fully covered by jros2.
- The services in the course (`/start_game`, `…/set_running`, `…/set_target_color`, `…/place`, …) connect the
  course's *own* Python nodes to each other. In the Java port all of that logic lives in one process, so these
  services become **plain Java method calls**.
- Arm actions (`FollowJointTrajectory`) → replaced by the servo topics.
- Where something *outside* must call into the Java app (OpenClaw, command line): REST/MCP, or a command topic
  (§4.3.4).

### Option B – Pure Java, no ROS

Java talks to the hardware directly: controller board over UART (jSerialComm), sonar and line sensor over I2C
(Pi4J), camera via OpenCV `VideoCapture`. More work, and the board protocol is **not on the wiki**
(see §4.4 for what is known), but no ROS/Docker dependency.

Build all code against an interface (`RobotIo`, §4.1) so you can start with A and switch to B later.

---

## 2. Python → Java library mapping

| Python (used in the course) | Purpose | Java equivalent | Maven coordinates |
|---|---|---|---|
| `rclpy`, `launch`, `launch_ros` | ROS 2 nodes, topics (services/actions: see §4.3.4) | **jros2** (IHMC): `ROS2Node`, `ROS2Topic`, `ROS2Publisher`, `createSubscription`; launch files → plain `main()` / one Java process | `us.ihmc:jros2:1.5.1` (IHMC repo `https://robotlabfiles.ihmc.us/repository/`; Maven Central only has 1.0.1) |
| ROS message packages (`geometry_msgs`, `sensor_msgs`, `std_msgs`) | Message classes | Bundled in jros2 as `<package>.<Type>`, e.g. `sensor_msgs.Image`, `std_msgs.Int32`, `std_msgs.String_` | jros2 |
| Custom msgs (`ros_robot_controller_msgs`, `servo_controller_msgs`, …) | Message classes | jros2 generator (Gradle plugin, run once; output committed into the Maven project, §3.2; `.msg` only, no `.srv`/`.action`) | `us.ihmc.jros2.generator` |
| `cv2` (OpenCV) | Image processing | **OpenCV Java bindings** (same API names) or **JavaCV** (bundles native libs incl. linux-arm64) | `org.openpnp:opencv:4.9.0-0` or `org.bytedeco:javacv-platform:1.5.10` |
| `cv_bridge.CvBridge` | `sensor_msgs/Image` ↔ `Mat` | Own 10-line converter (`byte[]` → `Mat`, see §4.3) | – |
| `numpy` | Arrays, masks | `Mat` operations; for general math ND4J or plain arrays | `org.nd4j:nd4j-native-platform` (rarely needed) |
| `pandas` (moving average in avoidance) | Filtering | Ring buffer or `DescriptiveStatistics(window)` | `org.apache.commons:commons-math3:3.6.1` |
| `yaml`, `sdk.yaml_handle` | LAB thresholds, servo offsets | SnakeYAML or Jackson YAML | `org.yaml:snakeyaml:2.2` / `jackson-dataformat-yaml` |
| `threading`, `queue` | Worker threads, frame queue | `java.util.concurrent` (`ExecutorService`, virtual threads, `ArrayBlockingQueue`, `ReentrantLock`) | JDK |
| `signal.signal(SIGINT, …)` | Stop robot on Ctrl+C | `Runtime.getRuntime().addShutdownHook(...)` | JDK |
| `time.sleep`, ROS timers | Timing | `ScheduledExecutorService`, `Thread.sleep` | JDK |
| `sdk.pid.PID` | PID controller | Own class (§4.2) | – |
| `sdk.Misc.map` | Range mapping | One static method | – |
| `sdk.sonar.Sonar`, `sdk.FourInfrared` | I2C ultrasonic + 4-ch line sensor | **Pi4J 2.x** I2C | `com.pi4j:pi4j-core`, `com.pi4j:pi4j-plugin-linuxfs` (Pi 5: use a Pi4J release with Pi 5 support) [verify version] |
| `pyserial` | UART (controller board, WonderEcho Pro) | **jSerialComm** | `com.fazecast:jSerialComm:2.10.4` |
| `pinctrl FAN_PWM …` (shell) | Fan on/off | `ProcessBuilder("pinctrl", …)` or Pi4J GPIO | JDK |
| `pyzbar` (QR, 4.6) | QR decoding | **ZXing** or OpenCV `QRCodeDetector` | `com.google.zxing:core:3.5.3`, `com.google.zxing:javase:3.5.3` |
| `mediapipe` | Hands/pose/face landmarks | **No official Java desktop/ARM API.** Use ONNX/TFLite-converted MediaPipe models with ONNX Runtime Java, or a Python sidecar (§7) | `com.microsoft.onnxruntime:onnxruntime:1.18.0` |
| `ultralytics` / `torch` (YOLO26) | Object detection | **Training stays in Python.** Export `.pt` → ONNX, infer with ONNX Runtime Java, OpenCV DNN or DJL | ONNX Runtime (above), `ai.djl:api` + `ai.djl.onnxruntime:onnxruntime-engine` |
| `labelImg`, `xml2yolo.py` | Annotation, VOC → YOLO | Keep labelImg; `xml2yolo` is easy in Java (DOM parser) | JDK |
| `openai` | LLM, Whisper ASR, TTS | **openai-java** (official) or Spring AI / LangChain4j | `com.openai:openai-java` [verify version]; `dev.langchain4j:langchain4j-open-ai` |
| OpenAI Realtime ASR (`RealTimeOpenAIASR`) | Streaming speech-to-text with VAD | Realtime API over `java.net.http.WebSocket` | JDK |
| `dashscope` (Qwen) | LLM / VLM | DashScope Java SDK or its OpenAI-compatible endpoint via openai-java | `com.alibaba:dashscope-sdk-java` [verify version] |
| OpenRouter (`vllm_*` keys) | Vision LLM | OpenAI-compatible → openai-java with custom `baseUrl` | – |
| `pyaudio`, `wave`, `arecord`/`aplay` | Mic capture, playback | `javax.sound.sampled` (`TargetDataLine`, `SourceDataLine`, `AudioSystem.write`) | JDK |
| `speech.awake.WonderEchoPro` | Wake word via serial | jSerialComm + frame match `AA 55 03 00 FB` (§10.1) | – |
| `re`, `json` | Parse LLM JSON | `java.util.regex`, Jackson | – |
| Telegram (OpenClaw) | Chat channel | Keep OpenClaw; if replaced: TelegramBots | `org.telegram:telegrambots-longpolling` [verify] |
| Wi-Fi tools (`wifi_conf.py`, `hw_wifi.service`) | AP/STA switch | Keep; or call `nmcli` via `ProcessBuilder` | – |

---

## 3. Project setup (Maven)

The app is built with **Maven**. One catch: jros2's generator for custom message classes exists only as a
**Gradle plugin** (`us.ihmc.jros2.generator`). Hiwonder's message definitions change rarely, so the classes are
generated **once** with a tiny helper project and the resulting `.java` files are committed into the Maven project
(`src/main/generated-ros/`). Maven itself never needs Gradle.

```
turbopi-java/
├── pom.xml
├── src/main/java/…                   your code
├── src/main/generated-ros/…          generated message classes (committed)
└── tools/ros-msg-gen/                one-off generator (Gradle, only run when messages change)
    ├── settings.gradle.kts
    ├── build.gradle.kts
    └── ros_interfaces/               copied from the robot: ros_robot_controller_msgs/, servo_controller_msgs/
```

### 3.1 `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>dev.turbopi</groupId>
  <artifactId>turbopi-java</artifactId>
  <version>0.1.0</version>

  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <jros2.version>1.5.1</jros2.version>
    <pi4j.version><!-- current release with Pi 5 support --></pi4j.version>
    <openai.version><!-- current release from Maven Central --></openai.version>
  </properties>

  <repositories>
    <!-- jros2 releases newer than 1.0.1 are only in IHMC's repository -->
    <repository>
      <id>ihmc-repo</id>
      <url>https://robotlabfiles.ihmc.us/repository/</url>
    </repository>
  </repositories>

  <dependencies>
    <!-- ROS 2 -->
    <dependency><groupId>us.ihmc</groupId><artifactId>jros2</artifactId><version>${jros2.version}</version></dependency>

    <!-- Vision (alternative: org.bytedeco:javacv-platform:1.5.11, used in jros2's webcam example) -->
    <dependency><groupId>org.openpnp</groupId><artifactId>opencv</artifactId><version>4.9.0-0</version></dependency>
    <dependency><groupId>com.google.zxing</groupId><artifactId>core</artifactId><version>3.5.3</version></dependency>
    <dependency><groupId>com.google.zxing</groupId><artifactId>javase</artifactId><version>3.5.3</version></dependency>

    <!-- ML inference (YOLO, MediaPipe models as ONNX) -->
    <dependency><groupId>com.microsoft.onnxruntime</groupId><artifactId>onnxruntime</artifactId><version>1.18.0</version></dependency>

    <!-- Hardware (option B, WonderEcho Pro) -->
    <dependency><groupId>com.fazecast</groupId><artifactId>jSerialComm</artifactId><version>2.10.4</version></dependency>
    <dependency><groupId>com.pi4j</groupId><artifactId>pi4j-core</artifactId><version>${pi4j.version}</version></dependency>
    <dependency><groupId>com.pi4j</groupId><artifactId>pi4j-plugin-linuxfs</artifactId><version>${pi4j.version}</version></dependency>

    <!-- JSON / YAML / math -->
    <dependency><groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-databind</artifactId><version>2.17.2</version></dependency>
    <dependency><groupId>com.fasterxml.jackson.dataformat</groupId><artifactId>jackson-dataformat-yaml</artifactId><version>2.17.2</version></dependency>
    <dependency><groupId>org.apache.commons</groupId><artifactId>commons-math3</artifactId><version>3.6.1</version></dependency>

    <!-- LLM / speech -->
    <dependency><groupId>com.openai</groupId><artifactId>openai-java</artifactId><version>${openai.version}</version></dependency>
  </dependencies>

  <build>
    <plugins>
      <!-- compile the generated ROS message classes as a second source root -->
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>build-helper-maven-plugin</artifactId>
        <version>3.6.0</version>
        <executions>
          <execution>
            <id>add-ros-messages</id>
            <phase>generate-sources</phase>
            <goals><goal>add-source</goal></goals>
            <configuration>
              <sources><source>src/main/generated-ros</source></sources>
            </configuration>
          </execution>
        </executions>
      </plugin>

      <!-- runnable fat jar for the Pi: java -jar target/turbopi-java-0.1.0.jar -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.6.0</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals><goal>shade</goal></goals>
            <configuration>
              <transformers>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                  <mainClass>dev.turbopi.Main</mainClass>
                </transformer>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
              </transformers>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

`1.5.1` is the version named in the jros2 README/wiki; Maven Central only lists 1.0.1, newer releases come from
the IHMC repository (`mvn dependency:resolve` shows whether 1.5.1 resolves). Run on the Pi with an ARM64 JDK 21
(e.g. Eclipse Temurin `21-jre`, `linux/arm64`) – jros2 ships native Fast-DDS libraries for linux-arm64.
Check that the shaded jar keeps jros2's native libraries (`jar tf target/*.jar | grep -i fastdds`).

### 3.2 One-off message generation (`tools/ros-msg-gen/`)

Run only when the robot's `.msg` files change. Needs a JDK and the Gradle wrapper (`gradle wrapper` once), not
part of the Maven build.

`settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://robotlabfiles.ihmc.us/repository/") }
    }
}
rootProject.name = "ros-msg-gen"
```

`build.gradle.kts`:

```kotlin
import us.ihmc.jros2.generator.jros2GenTask

plugins {
    java
    id("us.ihmc.jros2.generator") version "1.5.1"
}

tasks.register<jros2GenTask>("generateRobotMessages") {
    packagePaths = listOf(
        "ros_interfaces/ros_robot_controller_msgs",
        "ros_interfaces/servo_controller_msgs",
    ).map { projectDir.resolve(it).absolutePath }
    // write straight into the Maven project
    outputDir = projectDir.resolve("../../src/main/generated-ros").absolutePath
    cleanOutputDir = true          // the folder holds generated files only
}
```

```bash
cd tools/ros-msg-gen && ./gradlew generateRobotMessages
cd ../.. && mvn package
```

**Without Gradle at all:** jros2 also allows hand-written message classes (wiki "Approach 2": implement
`ROS2Message<T>` with the DDS type name, e.g. `"ros_robot_controller_msgs::msg::dds_::BuzzerState_"`, plus CDR
`serialize`/`deserialize`). Feasible for the 4–5 small Hiwonder messages, but error-prone – a wrong field order
silently breaks the wire format. Prefer the generator.

OpenCV Java needs the native lib loaded once:

```java
nu.pattern.OpenCV.loadLocally();   // org.openpnp:opencv
```

---

## 4. Common building blocks

### 4.1 `RobotIo` – one interface for all demos

Everything the course does to the robot boils down to these calls:

```java
public interface RobotIo extends AutoCloseable {
    // chassis – ROS: geometry_msgs/Twist on /cmd_vel  [wiki]
    void cmdVel(double linearX, double linearY, double angularZ);
    default void stop() { cmdVel(0, 0, 0); }

    // PWM servos – ROS: ros_robot_controller_msgs/SetPWMServoState  [wiki: type name; fields verify]
    void setPwmServos(double durationSec, ServoPos... positions);
    record ServoPos(int id, int pulse) {}          // pulse 500..2500, 1500 = 90°

    // RGB LEDs on the board – /ros_robot_controller/set_rgb, RGBStates{index, red, green, blue}  [wiki]
    void setRgb(Rgb... leds);
    record Rgb(int index, int r, int g, int b) {}

    // Buzzer – /ros_robot_controller/set_buzzer, BuzzerState{freq, on_time, off_time, repeat}  [wiki]
    void buzzer(int freqHz, double onSec, double offSec, int repeat);

    // Sensors
    int sonarDistanceMm();                         // sonar_controller/get_distance (Int32)  [wiki]
    void setSonarRgb(int index, int r, int g, int b);
    boolean[] lineSensor();                        // 4 channels, FourInfrared.readData()  [wiki]

    // Camera
    void onFrame(java.util.function.Consumer<org.opencv.core.Mat> consumer);
}
```

Implementations:

- `Jros2RobotIo` – option A, ROS 2 via jros2 (§4.3).
- `DirectRobotIo` – option B: jSerialComm + Pi4J + `VideoCapture` (§4.4).
- `FakeRobotIo` – for unit tests on the laptop (record calls, play back video files).

### 4.2 PID (`sdk.pid.PID`) and `Misc.map`

Hiwonder's `PID` is a classic positional PID with `SetPoint` and `update(feedback)` [verify against `sdk/pid.py`]:

```java
public final class Pid {
    private final double kp, ki, kd;
    private double setPoint, iTerm, lastError, windupGuard = 20.0;
    private long lastTime = System.nanoTime();
    private double output;

    public Pid(double kp, double ki, double kd) { this.kp = kp; this.ki = ki; this.kd = kd; }

    public void setPoint(double sp) { this.setPoint = sp; }
    public double output() { return output; }

    public double update(double feedback) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        double error = setPoint - feedback;
        iTerm = Math.clamp(iTerm + error * dt, -windupGuard, windupGuard);
        double dTerm = dt > 0 ? (error - lastError) / dt : 0;
        lastTime = now; lastError = error;
        output = kp * error + ki * iTerm + kd * dTerm;
        return output;
    }

    public void reset() { iTerm = 0; lastError = 0; output = 0; lastTime = System.nanoTime(); }
}

public final class Misc {
    public static double map(double x, double inMin, double inMax, double outMin, double outMax) {
        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }
}
```

### 4.3 Option A: ROS 2 with jros2

Source: jros2 README and wiki (https://github.com/ihmcrobotics/jros2). jros2 API calls below are taken from there.
Accessor names of *generated* message classes (`getLinear()`, `getStates().add()`, …) follow IHMC's generator
conventions but are **not shown in the jros2 docs** – check them in the generated sources. [verify]

**Naming:** jros2 uses the ROS package name as the Java package (`geometry_msgs`, `ros_robot_controller_msgs`, …).
These names are fixed by ROS and by the classes bundled in jros2, so they cannot be changed to camelCase. The code
examples import the classes (`import ros_robot_controller_msgs.SetPWMServoState;`) and use only the class names.
Strings like topic names (`/ros_robot_controller/set_rgb`), YAML keys and LLM action names keep their snake_case,
because they must match the robot.

#### 4.3.1 Message classes

- **Standard messages are bundled** (from `ros2/common_interfaces`, `example_interfaces`, `rcl_interfaces`),
  named `<package>.<Type>` – e.g. `sensor_msgs.Image`, `std_msgs.Int32`, `std_msgs.Bool`, `geometry_msgs.Twist`.
  Java-reserved names get an underscore: `std_msgs.String_`, `std_msgs.Byte_`.
- **Hiwonder's custom messages must be generated.** Copy the interface packages from the robot into
  `tools/ros-msg-gen/ros_interfaces/` (each folder needs its `package.xml` and `msg/`):

  ```bash
  # inside the TurboPi container
  find ~/ros2_ws/src -name package.xml -path "*msgs*"     # locate ros_robot_controller_msgs, servo_controller_msgs, …
  ```

  `./gradlew generateRobotMessages` in `tools/ros-msg-gen` (§3.2) then writes into `src/main/generated-ros/` e.g. `ros_robot_controller_msgs.RGBStates`,
  `ros_robot_controller_msgs.BuzzerState`, `ros_robot_controller_msgs.SetPWMServoState`,
  `servo_controller_msgs.ServosPosition`.
  **Only `.msg` files are generated – `.srv` and `.action` are skipped** (packages that contain only services,
  like `large_models_msgs`, are not needed at all; see §4.3.4).

#### 4.3.2 Node, publishers, subscriptions

```java
import geometry_msgs.Twist;
import ros_robot_controller_msgs.BuzzerState;
import ros_robot_controller_msgs.PWMServoState;
import ros_robot_controller_msgs.RGBState;
import ros_robot_controller_msgs.RGBStates;
import ros_robot_controller_msgs.SetPWMServoState;
import sensor_msgs.Image;
import std_msgs.Int32;

import us.ihmc.jros2.*;

public final class Jros2RobotIo implements RobotIo {
    private final ROS2Node node;
    private final ROS2Publisher<Twist> cmdVelPub;
    private final ROS2Publisher<RGBStates> rgbPub;
    private final ROS2Publisher<BuzzerState> buzzerPub;
    private final ROS2Publisher<SetPWMServoState> servoPub;
    private final Twist twist = new Twist();        // reused, no allocation per call
    private volatile int sonarMm = -1;
    private volatile Consumer<Mat> frameConsumer = m -> { };

    public Jros2RobotIo(int domainId) {
        node = new ROS2Node("turbopi_java", domainId);                          // must match ROS_DOMAIN_ID of the container

        cmdVelPub = node.createPublisher(new ROS2Topic<>("/cmd_vel", Twist.class));
        rgbPub    = node.createPublisher(new ROS2Topic<>("/ros_robot_controller/set_rgb",
                                                         RGBStates.class));
        buzzerPub = node.createPublisher(new ROS2Topic<>("/ros_robot_controller/set_buzzer",
                                                         BuzzerState.class));
        servoPub  = node.createPublisher(new ROS2Topic<>("/ros_robot_controller/pwm_servo/set_state",   // topic name [verify]
                                                         SetPWMServoState.class));

        // sonar distance – std_msgs/Int32 [wiki]; callbacks run on jros2 threads → volatile / locks
        node.createSubscription(
            new ROS2Topic<>("/sonar_controller/get_distance", Int32.class),
            reader -> sonarMm = reader.read().getData());

        // camera – best effort matches both reliable and best-effort publishers
        ROS2Topic<Image> imageTopic = new ROS2Topic<>("/image_raw", Image.class)
                             .withQoS(ROS2QoSProfile.BEST_EFFORT);
        Image img = new Image();                                       // reused message object
        node.createSubscription(imageTopic, reader -> {
            reader.read(img);
            frameConsumer.accept(ImageConv.toMat(img));
        });
    }

    @Override public void cmdVel(double x, double y, double z) {
        synchronized (twist) {
            twist.getLinear().setX(x);                                           // accessor names [verify]
            twist.getLinear().setY(y);
            twist.getAngular().setZ(z);
            cmdVelPub.publish(twist);                                            // publish is thread-safe
        }
    }

    @Override public void setRgb(Rgb... leds) {
        RGBStates msg = new RGBStates();
        for (Rgb l : leds) {
            RGBState s = msg.getStates().add();                                       // sequence accessor [verify]
            s.setIndex(l.index()); s.setRed(l.r()); s.setGreen(l.g()); s.setBlue(l.b());
        }
        rgbPub.publish(msg);
    }

    @Override public void buzzer(int freq, double on, double off, int repeat) {
        BuzzerState msg = new BuzzerState();
        msg.setFreq(freq); msg.setOnTime((float) on); msg.setOffTime((float) off); msg.setRepeat(repeat);
        buzzerPub.publish(msg);
    }

    @Override public void setPwmServos(double durationSec, ServoPos... positions) {
        SetPWMServoState msg = new SetPWMServoState();
        msg.setDuration(durationSec);
        for (ServoPos p : positions) {
            PWMServoState s = msg.getState().add();
            s.getId().add(p.id());                                               // field layout [verify with
            s.getPosition().add(p.pulse());                                      //  ros2 interface show …]
        }
        servoPub.publish(msg);
    }

    @Override public int sonarDistanceMm() { return sonarMm; }
    @Override public void onFrame(Consumer<Mat> c) { frameConsumer = c; }
    @Override public void close() { stop(); node.close(); }
    // setSonarRgb, lineSensor: same pattern with the topics listed by `ros2 topic list -t`
}
```

Always check topic names and field layouts on the robot first:
`ros2 topic list -t`, `ros2 interface show ros_robot_controller_msgs/msg/SetPWMServoState`.

#### 4.3.3 `sensor_msgs/Image` ↔ OpenCV `Mat` (replaces `cv_bridge`)

In jros2, `Image.getData()` is a sequence wrapper whose `getBuffer()` returns a `ByteBuffer`; `getEncoding()` is a
`StringBuilder`-like value (from jros2's webcam example).

```java
import sensor_msgs.Image;

public final class ImageConv {
    public static Mat toMat(Image img) {
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        ByteBuffer buf = img.getData().getBuffer();
        byte[] px = new byte[w * h * 3];
        buf.rewind(); buf.get(px);
        Mat m = new Mat(h, w, CvType.CV_8UC3);
        m.put(0, 0, px);
        if ("rgb8".contentEquals(img.getEncoding())) Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2BGR);
        return m;                                                                // BGR like cv_bridge "bgr8"
    }

    public static void fromMat(Mat bgr, Image out) {                 // e.g. for ~/image_result
        byte[] px = new byte[(int) (bgr.total() * bgr.channels())];
        bgr.get(0, 0, px);
        out.getEncoding().setLength(0); out.getEncoding().append("bgr8");
        out.setWidth(bgr.cols()); out.setHeight(bgr.rows()); out.setStep(bgr.cols() * 3);
        out.getData().ensureMinCapacity(px.length);
        out.getData().getBuffer().rewind();
        out.getData().getBuffer().put(px);
    }
}
```

Raw 640×480 frames over DDS are fine on the same host. Alternative: stop the `usb_cam` node and open the camera
directly in Java (`new VideoCapture(0, Videoio.CAP_V4L2)`) – then only one process may use the camera.

#### 4.3.4 Services and actions (not in jros2 yet)

| Where the course uses a service/action | Java solution |
|---|---|
| Between the course's own nodes: `/start_game`, `/stop_game`, `/line_follow/set_running`, `/color_track_pick/set_target_color`, `/color_track_pick/place`, `/avoidance_node/set_param`, `agent_process/set_model`, `vocal_detect/enable_wakeup`, … | All these nodes are ported to Java and run in **one process** → plain method calls (`lineFollow.setRunning(true)`) |
| Starting/stopping a demo from the terminal (`ros2 service call …`) | CLI argument, or a **command topic**, e.g. `/turbopi_java/command` (`std_msgs/String`, `"start_game"`, `"set_target_color red"`), callable with `ros2 topic pub --once …` |
| Arm/gripper actions `/arm_controller/follow_joint_trajectory`, `/gripper_controller/…` | Use the servo topics instead (`/servo_controller` `ServosPosition`, or `SetPWMServoState` with pulses), §5.5 |
| External callers that need *real* ROS services (OpenClaw skills calling `/smart_logistics/*`) | REST or MCP endpoint in the Java app (§11), or a ~30-line `rclpy` shim node that turns each service call into a topic message for Java and waits for a reply topic |
| Calling a Hiwonder driver *service* (if one turns out to be needed) | Same shim node; switch to jros2 services once released |

#### 4.3.5 Configuration and networking

- **Domain ID**: constructor argument, `-Dros.domain.id=…`, `ROS_DOMAIN_ID`, or `jros2.ros.domain.id` in
  `jros2.properties` (working dir, `src/main/resources/` or `~/.ihmc/`). Must match the container.
- **QoS**: `ROS2QoSProfile.DEFAULT`, `RELIABLE`, `BEST_EFFORT`, or a custom profile
  (`reliability`, `durability`, `history`, `depth`); per topic via `.withQoS(…)` or as an argument to
  `createPublisher`/`createSubscription`.
- **Docker**: the Hiwonder nodes run in the `TurboPi` container. Run the Java app either inside the container or on
  the host with the container on `--network host`; otherwise DDS discovery fails. If Fast-DDS shared-memory
  transport causes problems across the container boundary, restrict to UDP / whitelist interfaces:
  `-Dfastdds.interface.whitelist=lo,eth0`.
- The container's RMW must be DDS-based (Fast DDS or Cyclone DDS – both interoperate over RTPS). `rmw_zenoh`
  would not be reachable from jros2. Check with `echo $RMW_IMPLEMENTATION` in the container.
- jros2 callbacks run on internal threads – hand frames to the processing thread (§6.1) and guard shared state.

### 4.4 Option B: talking to the hardware directly [verify – not on the wiki]

The wiki gives **no** UART port, baud rate or packet format for the controller board. What follows comes from
Hiwonder's `ros_robot_controller_sdk.py` as generally known. Read that file in the container
(`find ~/ros2_ws -name "ros_robot_controller_sdk.py"`) and correct this table before using it.

| Item | Value [verify] |
|---|---|
| Port / baud | `/dev/ttyAMA0`, 1 000 000 baud (Pi 5) |
| Frame | `0xAA 0x55 <func> <len> <data…> <crc8>`; CRC-8 (Dallas/Maxim) over `func, len, data` |
| Function codes | SYS=0, LED=1, BUZZER=2, MOTOR=3, PWM_SERVO=4, BUS_SERVO=5, KEY=6, IMU=7, GAMEPAD=8, SBUS=9, OLED=10, RGB=11 |
| Buzzer data | `<HHHH` freq, on_ms, off_ms, repeat (little endian) |
| Motor data | `0x01, n, {id-1: uint8, speed: float32 LE}…` |
| PWM servo data | `0x01, duration_ms: uint16, n, {id: uint8, pulse: uint16}…` |
| RGB data | `0x01, n, {index-1, r, g, b}…` |

```java
public final class ControllerBoard implements AutoCloseable {
    private final SerialPort port;

    public ControllerBoard(String dev, int baud) {
        port = SerialPort.getCommPort(dev);
        port.setComPortParameters(baud, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        if (!port.openPort()) throw new IllegalStateException("cannot open " + dev);
    }

    public synchronized void send(int func, byte[] data) {
        ByteBuffer buf = ByteBuffer.allocate(data.length + 5).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 0xAA).put((byte) 0x55).put((byte) func).put((byte) data.length).put(data);
        byte[] frame = Arrays.copyOf(buf.array(), data.length + 5);
        frame[frame.length - 1] = crc8(frame, 2, data.length + 2);
        port.writeBytes(frame, frame.length);
    }

    public void buzzer(int freq, double on, double off, int repeat) {
        ByteBuffer b = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
            .putShort((short) freq).putShort((short) (on * 1000))
            .putShort((short) (off * 1000)).putShort((short) repeat);
        send(2, b.array());
    }

    public void pwmServos(double durationSec, RobotIo.ServoPos... s) {
        ByteBuffer b = ByteBuffer.allocate(4 + 3 * s.length).order(ByteOrder.LITTLE_ENDIAN)
            .put((byte) 0x01).putShort((short) (durationSec * 1000)).put((byte) s.length);
        for (RobotIo.ServoPos p : s) b.put((byte) p.id()).putShort((short) p.pulse());
        send(4, b.array());
    }

    static byte crc8(byte[] d, int off, int len) {          // Dallas/Maxim, reflected poly 0x8C
        int crc = 0;
        for (int i = off; i < off + len; i++) {
            crc ^= d[i] & 0xFF;
            for (int k = 0; k < 8; k++) crc = (crc & 1) != 0 ? (crc >>> 1) ^ 0x8C : crc >>> 1;
        }
        return (byte) crc;
    }

    @Override public void close() { port.closePort(); }
}
```

**I2C sensors** (`sdk.sonar.Sonar`, `sdk.FourInfrared`) [verify addresses in `sdk/sonar.py` and `sdk/FourInfrared.py`]:
sonar commonly at `0x77` (distance as 16-bit LE in mm), 4-channel line sensor at `0x78`, register `0x01`,
one byte with one bit per channel.

```java
Context pi4j = Pi4J.newAutoContext();                    // com.pi4j.context.Context
I2C line = pi4j.create(I2C.newConfigBuilder(pi4j).id("line").bus(1).device(0x78).build());

boolean[] readLine() {
    int v = line.readRegister(0x01);
    return new boolean[] { (v & 1) != 0, (v & 2) != 0, (v & 4) != 0, (v & 8) != 0 };
}
```

Pi 5 fan (used in chapter 8 to silence the mic): `new ProcessBuilder("pinctrl","FAN_PWM","op","dh").start()` = off,
`"op","dl"` = on, `"a0"` = back to automatic [wiki].

---

## 5. Chapters 1–3: hardware, app, mecanum chassis

### 5.1 Hardware constants [wiki]

```java
public final class TurboPi {
    // Motors
    public static final int M1_FRONT_LEFT = 1, M2_FRONT_RIGHT = 2, M3_REAR_LEFT = 3, M4_REAR_RIGHT = 4;
    // PWM servo ports
    public static final int SERVO_PAN_TILT_PITCH = 1;   // up/down
    public static final int SERVO_PAN_TILT_YAW   = 2;   // left/right
    public static final int SERVO_ARM_LIFT       = 5;
    public static final int SERVO_GRIPPER        = 6;
    // Servo pulse: 500..2500 for 0..180°, 1500 = 90° (home)
    public static int angleToPulse(double deg) { return (int) Math.round(deg * 11.1 + 500); }   // [wiki-summary]
    public static final double LOW_BATTERY_VOLT = 7.1;  // buzzer beeps below this
}
```

Port 3 = ultrasonic (with 2 RGB LEDs), port 4 = 4-channel line sensor. Camera = USB.
Default network: AP mode, SSID `HW…`, password `hiwonder`, robot IP `192.168.149.1` [wiki].
Servo offsets are stored as YAML by `software/Servo_upper_computer/main.py` – read them with SnakeYAML
and add them to every pulse you send.

### 5.2 App modes (chapter 2)

The WonderPi app (`com.Wonder.Pi`) talks to the robot with a protocol the wiki does not document.
**A Java port cannot replace the app** without capturing that protocol. The *functions* behind the modes are
the same as chapter 4 (pan-tilt tracking = 4.4, color line following = 4.5, QR = 4.6, avoidance = 4.7) and are
ported there. If you want your own remote control, build a small web UI (Javalin or Spring Boot) with an MJPEG
stream (`multipart/x-mixed-replace`) and a joystick that calls `cmdVel`.

### 5.3 Mecanum demos (chapter 3) [wiki]

All demos publish `geometry_msgs/Twist` on `cmd_vel` once per second; Ctrl+C publishes a zero Twist.
Ranges: `linear.x`/`linear.y` −1…1 (visible motion above ~0.35), `angular.z` −10…10.

| Python script | Java equivalent (`io.cmdVel(x, y, z)`) |
|---|---|
| `car_forward_demo.py` | `(0.5, 0, 0)` |
| `car_turn_demo.py` | alternate `(0, 0, 5.0)` ↔ `(0, 0, -5.0)` |
| `car_move_demo.py` | cycle `(0.5,0,0)`, `(-0.5,0,0)`, `(0,0.5,0)`, `(0,-0.5,0)` |
| `car_slant_demo.py` | cycle `(0.5,0.5)`, `(0.5,-0.5)`, `(-0.5,0.5)`, `(-0.5,-0.5)` (45° diagonals), 1 s initial delay |
| `car_drifting_demo.py` | `(0.5, 0, 4.0)`, then clockwise with negative `angular.z` [wiki-summary] |

```java
public final class CarMoveDemo {
    public static void main(String[] args) throws Exception {
        try (RobotIo io = RobotIoFactory.create()) {
            Runtime.getRuntime().addShutdownHook(new Thread(io::stop));     // = signal.SIGINT handler
            double[][] steps = { {0.5, 0}, {-0.5, 0}, {0, 0.5}, {0, -0.5} };
            ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
            AtomicInteger i = new AtomicInteger();
            timer.scheduleAtFixedRate(() -> {                               // = create_timer(1.0, …)
                double[] s = steps[i.getAndIncrement() % steps.length];
                io.cmdVel(s[0], s[1], 0);
            }, 0, 1, TimeUnit.SECONDS);
            Thread.currentThread().join();                                  // = rclpy.spin
        }
    }
}
```

### 5.4 Mecanum kinematics (only needed for option B)

The wiki gives the translation part for wheels A–D (ABAB roller layout) [wiki]:
X: `VA = VB = VC = VD = +VX`; Y: `VA = −VY, VB = +VY, VC = −VY, VD = +VY`.
The rotation formula is an image that did not load. The standard inverse kinematics (L = half track width,
H = half wheelbase) is:

```java
/** returns wheel speeds {A, B, C, D}; sign pattern for ω must be checked on the real robot */
static double[] inverseKinematics(double vx, double vy, double omega, double l, double h) {
    double k = (l + h) * omega;
    return new double[] {
        vx - vy - k,   // A
        vx + vy + k,   // B
        vx - vy + k,   // C
        vx + vy - k,   // D
    };
}
```

The mapping A–D → M1–M4 is not stated on the wiki. Test it on the stand: drive pure `+VY` and `+ω`
and fix signs per motor. With ROS (option A) the controller node already does this.

### 5.5 Arm and gripper [wiki]

| Function | ROS 2 interface | Java (jros2) |
|---|---|---|
| Single servo | topic `/servo_controller`, `servo_controller_msgs/msg/ServosPosition` `{duration, position_unit:"pulse", position:[{id, position}]}` | publish generated `servo_controller_msgs.ServosPosition` (code below) |
| Arm trajectory | action `/arm_controller/follow_joint_trajectory`, `control_msgs/action/FollowJointTrajectory`, joints `joint1..joint5` | **no actions in jros2** → move the arm servos via `/servo_controller` with pulses |
| Gripper | action `/gripper_controller/follow_joint_trajectory`, joint `joint6`, e.g. `[0.5]` | same: servo 6 via `/servo_controller` |

```java
import servo_controller_msgs.ServoPosition;
import servo_controller_msgs.ServosPosition;

ROS2Topic<ServosPosition> servoTopic =
        new ROS2Topic<>("/servo_controller", ServosPosition.class);
ROS2Publisher<ServosPosition> servoPub = node.createPublisher(servoTopic);

void servo(int id, int pulse, double durationSec) {
    ServosPosition msg = new ServosPosition();
    msg.setDuration(durationSec);
    msg.getPositionUnit().append("pulse");                     // accessor names [verify in generated code]
    ServoPosition p = msg.getPosition().add();   // element type name [verify]
    p.setId(id); p.setPosition(pulse);
    servoPub.publish(msg);
}
// gripper close/open (wiki example): servo(6, 2500, 1.0); servo(6, 2000, 1.0);
```

Joint angles (rad) from the action examples → pulses: take the joint↔servo mapping and limits from the
controller's config on the robot. For option B, use `pwmServos(duration, new ServoPos(5, …), new ServoPos(6, …))` directly.

---

## 6. Chapter 4: OpenCV vision (OpenCV Java)

OpenCV Java has the same function names: `cv2.inRange` → `Core.inRange`, `cv2.cvtColor` → `Imgproc.cvtColor`,
`cv2.findContours(...)[-2]` → `Imgproc.findContours(src, contoursList, hierarchy, mode, method)`.
8-bit LAB in OpenCV Java uses the same scaling as Python (L 0–255, a/b offset 128), so the thresholds in
`/home/ubuntu/software/lab_tool/lab_config.yaml` can be reused unchanged.

### 6.1 Shared vision utilities

```java
public final class Vision {
    public record LabRange(Scalar min, Scalar max) {}

    /** lab_config.yaml: { color_range_list: { red: {min:[L,a,b], max:[L,a,b]}, … } }  [verify key names] */
    @SuppressWarnings("unchecked")
    public static Map<String, LabRange> loadLab(Path yaml) throws IOException {
        Map<String, Object> root = new Yaml().load(Files.newBufferedReader(yaml));
        Map<String, Map<String, List<Number>>> list =
                (Map<String, Map<String, List<Number>>>) root.getOrDefault("color_range_list", root);
        Map<String, LabRange> out = new LinkedHashMap<>();
        list.forEach((name, r) -> out.put(name, new LabRange(toScalar(r.get("min")), toScalar(r.get("max")))));
        return out;
    }
    private static Scalar toScalar(List<Number> v) {
        return new Scalar(v.get(0).doubleValue(), v.get(1).doubleValue(), v.get(2).doubleValue());
    }

    /** resize → GaussianBlur(3x3, σ3) → BGR2LAB  (lessons 4.1, 4.2, 4.8, 4.10) */
    public static Mat preprocess(Mat bgr, Size size) {
        Mat small = new Mat(), blur = new Mat(), lab = new Mat();
        Imgproc.resize(bgr, small, size, 0, 0, Imgproc.INTER_NEAREST);
        Imgproc.GaussianBlur(small, blur, new Size(3, 3), 3);
        Imgproc.cvtColor(blur, lab, Imgproc.COLOR_BGR2LAB);
        return lab;
    }

    /** inRange → erode(3x3 rect) → dilate(3x3 rect) */
    public static Mat mask(Mat lab, LabRange r) {
        Mat m = new Mat();
        Core.inRange(lab, r.min(), r.max(), m);
        Mat k = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(m, m, k);
        Imgproc.dilate(m, m, k);
        return m;
    }

    /** open + close with np.ones((3,3)) – variant used in 4.3 and 4.8 */
    public static Mat maskOpenClose(Mat lab, LabRange r) {
        Mat m = new Mat();
        Core.inRange(lab, r.min(), r.max(), m);
        Mat k = Mat.ones(3, 3, CvType.CV_8U);
        Imgproc.morphologyEx(m, m, Imgproc.MORPH_OPEN, k);
        Imgproc.morphologyEx(m, m, Imgproc.MORPH_CLOSE, k);
        return m;
    }

    /** get_area_max_contour(contours, minArea) */
    public record Blob(MatOfPoint contour, double area) {}
    public static Optional<Blob> largestContour(Mat mask, double minArea, int approx) {
        List<MatOfPoint> cs = new ArrayList<>();
        Imgproc.findContours(mask, cs, new Mat(), Imgproc.RETR_EXTERNAL, approx);
        return cs.stream()
                 .map(c -> new Blob(c, Math.abs(Imgproc.contourArea(c))))
                 .filter(b -> b.area() > minArea)
                 .max(Comparator.comparingDouble(Blob::area));
    }

    public static Point center(MatOfPoint c, float[] radiusOut) {
        Point p = new Point();
        Imgproc.minEnclosingCircle(new MatOfPoint2f(c.toArray()), p, radiusOut);
        return p;
    }
}
```

Frame handling as in the Python lessons ("callback stores latest frame under a lock, worker loop processes it"):

```java
private final AtomicReference<Mat> latest = new AtomicReference<>();
io.onFrame(latest::set);                                   // image_callback
Thread.ofVirtual().start(() -> {                           // processing loop
    while (running) {
        Mat f = latest.getAndSet(null);
        if (f == null) { Thread.onSpinWait(); continue; }
        Mat result = run(f);
        if (debug) { HighGui.imshow("Frame", result); if (HighGui.waitKey(1) == 27) running = false; }
    }
});
```

`HighGui` needs a desktop (VNC). On a headless robot, stream the result as MJPEG instead.

### 6.2 Lesson 4.1 – single color recognition (`color_warning.py`) [wiki]

Pipeline: blur → LAB → for every color except black/white: mask → largest contour (area > 50) → draw circle,
print color; buzzer rings once when the color is red.

```java
// labRanges = Vision.loadLab(Path.of("/home/ubuntu/software/lab_tool/lab_config.yaml"))
String detect(Mat frame) {
    Mat lab = Vision.preprocess(frame, frame.size());
    String best = null; double bestArea = 0;
    for (Map.Entry<String, Vision.LabRange> e : labRanges.entrySet()) {
        if (e.getKey().equals("black") || e.getKey().equals("white")) continue;
        Optional<Vision.Blob> blob = Vision.largestContour(Vision.mask(lab, e.getValue()), 50, Imgproc.CHAIN_APPROX_NONE);
        if (blob.isPresent() && blob.get().area() > bestArea) { best = e.getKey(); bestArea = blob.get().area(); }
    }
    if ("red".equals(best) && !buzzed) { io.buzzer(1900, 0.2, 0.01, 1); buzzed = true; }   // values [wiki-summary]
    return best;
}
```

Drawing colors (BGR): red `(0,0,255)`, green `(0,255,0)`, blue `(255,0,0)`, black `(0,0,0)`.

### 6.3 Lesson 4.2 – color recognition with reactions (`color_recognize.py`) [wiki]

Resize to 320×240, same pipeline, pick the color with the largest area (detection if area > 2000 [wiki-summary]).
Red → nod; green/blue → shake head; buzzer once; RGB LEDs in that color.

```java
void nod()   { seq(SERVO_PAN_TILT_PITCH, 1800, 1200, 1500); }
void shake() { seq(SERVO_PAN_TILT_YAW,   1200, 1800, 1500); }
private void seq(int id, int... pulses) {
    for (int p : pulses) { io.setPwmServos(0.2, new ServoPos(id, p)); sleep(300); }
}
void react(String color) {
    Rgb[] c = switch (color) {
        case "red"   -> rgbBoth(255, 0, 0);
        case "green" -> rgbBoth(0, 255, 0);
        case "blue"  -> rgbBoth(0, 0, 255);
        default      -> rgbBoth(0, 0, 0);
    };
    io.setRgb(c);
    io.buzzer(1900, 0.1, 0.9, 1);
    if (color.equals("red")) nod(); else shake();
}
```

### 6.4 Lesson 4.3 – colored block positioning [wiki-summary]

Same as 4.2 with `maskOpenClose`, then draw the rectangle/circle and a label
(`Imgproc.putText(img, text, org, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)`) with the block's pixel coordinates.

### 6.5 Lesson 4.4 – target tracking (`tracking.py`) [wiki-summary]

- Blur 5×5 σ5, RGB→LAB. Threshold window around a picked LAB color:
  `min = target − (100·t for L, 50·t for a/b)`, `max = target + …` (t = tolerance).
- Contours with area > 40; choose the largest, then the one nearest to the previous center (max 100 px jump).
- `minEnclosingCircle` → center (x, y), radius.
- Pan-tilt: two `Pid`s, error = center − image center, output added to servo pulses (yaw: servo 2, pitch: servo 1),
  clamp to 500–2500.
- Chassis following: yaw error → `angular.z`, radius error → `linear.x`; pan-tilt fixed at (1500, 1600).
- Stop after 5 s without target.

```java
Pid yaw = new Pid(0.06, 0, 0.002), pitch = new Pid(0.06, 0, 0.002);   // gains not on wiki – tune
int yawPulse = 1500, pitchPulse = 1500;

void track(Point c, double w, double h) {
    yaw.setPoint(w / 2);   yawPulse   = clamp(yawPulse   + (int) yaw.update(c.x),   500, 2500);
    pitch.setPoint(h / 2); pitchPulse = clamp(pitchPulse - (int) pitch.update(c.y), 500, 2500);
    io.setPwmServos(0.02, new ServoPos(2, yawPulse), new ServoPos(1, pitchPulse));
}
```

### 6.6 Lesson 4.5 – visual line following (`line_following.py`) [wiki-summary]

- Click the line color in the result window (`EVENT_LBUTTONDOWN` → normalized x,y → `SetPoint` service).
  In Java: a mouse listener on your own window, or a web UI click → read LAB at that pixel → build the range.
- Blur 3×3 σ3, RGB→LAB, several horizontal ROI strips `img[roi0*h : roi1*h, roi2*w : roi3*w]`
  (`lab.submat(rowStart, rowEnd, colStart, colEnd)`), each weighted.
- Per ROI: largest contour (`CHAIN_APPROX_TC89_L1`), `minAreaRect` → `boxPoints` → center.
- Weighted center x → `Pid` → `angular.z`; constant `linear.x`.
- ROI values and weights are not on the wiki – take them from `line_following.py` on the robot.

```java
RotatedRect r = Imgproc.minAreaRect(new MatOfPoint2f(blob.contour().toArray()));
Point[] box = new Point[4]; r.points(box);                       // cv2.boxPoints
double cx = (box[0].x + box[2].x) / 2, cy = (box[0].y + box[2].y) / 2;
Imgproc.drawContours(img, List.of(new MatOfPoint(box)), -1, new Scalar(0, 255, 255), 2);
```

### 6.7 Lesson 4.6 – QR code recognition

Page section not readable. The app demo: robot recognizes a QR code (max ≈ 35 cm) and runs the matching action
[wiki, chapter 2]. Java:

```java
QRCodeDetector qr = new QRCodeDetector();                // OpenCV built-in, no extra lib
Mat pts = new Mat();
String text = qr.detectAndDecode(frame, pts);
if (!text.isEmpty()) runAction(text);                    // e.g. "forward", "left", … – check app/qrcode.py
```

ZXing (`MultiFormatReader` on a `BufferedImageLuminanceSource`) is more robust at angles and distances.

### 6.8 Lesson 4.7 – ultrasonic obstacle avoidance (`avoidance_node.py`) [wiki]

Defaults: `threshold = 30` cm, `speed = 0.4`, start `distance = 500`. Distance is filtered: value/4 [sic, from
code] → window of 5 → outliers outside 1σ dropped → mean. If mean ≤ threshold: turn (`angular.z = 11.0` in the code,
7.0 in the text); else forward with `linear.x = speed`. Commands are only sent on state change. Services:
`enter`, `exit`, `set_running` (SetBool), `set_param` (`[threshold, speed]`).

```java
public final class Avoidance {
    private final DescriptiveStatistics window = new DescriptiveStatistics(5);
    private double threshold = 30, speed = 0.4;
    private Boolean turning = null;

    void onDistance(int raw) {
        window.addValue(raw / 4.0);                                 // keep the /4 of the original only if your
        double mean = window.getMean(), sd = window.getStandardDeviation();   // source sends raw*4 – verify
        double filtered = Arrays.stream(window.getValues())
                                .filter(v -> Math.abs(v - mean) <= sd || sd == 0)
                                .average().orElse(mean);
        boolean mustTurn = filtered <= threshold;
        if (Objects.equals(turning, mustTurn)) return;              // only on change
        turning = mustTurn;
        if (mustTurn) io.cmdVel(0, 0, 11.0); else io.cmdVel(speed, 0, 0);
    }
    void setParam(double threshold, double speedPercent) { this.threshold = threshold; this.speed = speedPercent / 100; }
}
```

Overlay: `putText(img, String.format("Dist:%.1fcm", d), new Point(30, 450), FONT_HERSHEY_SIMPLEX, 1.2, new Scalar(255,255,0), 2)`.
On exit: sonar RGB LEDs 0 and 1 off.

### 6.9 Lesson 4.8 – traffic light + line sensor (`line_follower.py`) [wiki]

- Camera: resize, blur, LAB, `maskOpenClose`, contours `CHAIN_APPROX_SIMPLE`, keep area > 300, detection if
  largest > 2500, draw `minAreaRect` box `(0,255,0)` and label `"Color: …"` at `(10, h−10)`, scale 0.65.
- Red → stop, green → drive, yellow → slow/attention; RGB LEDs 1 and 2 show the color;
  `buzzer(1000, 0.1, 0.1, 1)`.
- Line sensor (4 channels) decision table. The Python code inverts the raw values (`not s`), but the wiki also
  says the sensor reads 1 over black – **check on the robot which is right**. Several Python branches are
  unreachable duplicates; this is the cleaned table (`true` = sensor over the line):

```java
void followLine(boolean[] s) {                      // s = {S0, S1, S2, S3}, left → right
    String p = (s[0]?"1":"0") + (s[1]?"1":"0") + (s[2]?"1":"0") + (s[3]?"1":"0");
    switch (p) {
        case "0110"         -> io.cmdVel(0.3, 0, 0);     // centered → forward
        case "0100", "1100" -> io.cmdVel(0.2, 0, 2.0);   // line left → turn left
        case "0010", "0011" -> io.cmdVel(0.2, 0, -2.0);  // line right → turn right
        case "1000", "1110" -> io.cmdVel(0.0, 0, 4.0);   // far left → sharp left
        case "0001", "0111" -> io.cmdVel(0.0, 0, -4.0);  // far right → sharp right
        case "1111"         -> io.stop();                // crossing / stop line
        default             -> io.stop();                // lost
    }
}
```

Speeds are examples – the original values are in the source file. Start/stop via services `/start_game`,
`/stop_game` (`example_interfaces/srv/SetBool`) – in Java simply expose `start()`/`stop()`; from outside via the
command topic (§4.3.4), since jros2 has no services yet.

### 6.10 Lesson 4.9 – line-following transport (`line_follow_transport.py`) [wiki]

Coordinator state machine: `IDLE → STARTING → WAITING_PICK → WAITING_CROSSROADS → PLACING → IDLE`, ticked by a timer.
Start: wait for both init services, set target color `red`, enable the picker, after 2 s start line following.
After a pick: send `stop_next_road` to line following (stop at the next crossing). In PLACING, once the picker is
IDLE and has nothing picked, stop line following and disable the picker.

```java
enum TransportState { IDLE, STARTING, WAITING_PICK, WAITING_CROSSROADS, PLACING }

void tick() {
    switch (state) {
        case STARTING -> { picker.setTargetColor("red"); picker.setRunning(true);
                           scheduler.schedule(lineFollow::start, 2, TimeUnit.SECONDS);
                           state = TransportState.WAITING_PICK; }
        case WAITING_PICK -> { if (picker.hasPicked()) { lineFollow.stopAtNextCrossing();
                                                         state = TransportState.WAITING_CROSSROADS; } }
        case WAITING_CROSSROADS -> { if (lineFollow.isStopped()) { picker.place(); state = TransportState.PLACING; } }
        case PLACING -> { if (picker.isIdle() && !picker.hasPicked()) {
                              lineFollow.stop(); picker.setRunning(false); state = TransportState.IDLE; } }
        case IDLE -> { }
    }
}
```

In a pure Java port these are plain objects in one process – no ROS services needed between them.

### 6.11 Lesson 4.10 – visual tracking and grasping (`color_track_pick.py`) [wiki]

- Image 320×240; mask as in 6.1; then a centered ROI mask (~90 % width, ~80 % height):

```java
Mat roi = Mat.zeros(240, 320, CvType.CV_8U);
int rx = (320 - ROI_W) / 2, ry = (240 - ROI_H) / 2;
Imgproc.rectangle(roi, new Point(rx, ry), new Point(rx + ROI_W, ry + ROI_H), new Scalar(255), -1);
Core.bitwise_and(mask, mask, mask, roi);
```

- Prefer the contour closest to the vertical center line; `minEnclosingCircle` → (cx, cy, r).
- States `IDLE, TRACKING, APPROACHING, PICKING, PLACING`:
  - TRACKING: `Pid` on `cx − IMG_CENTER_X` → `angular.z` until centered.
  - APPROACHING: drive forward while `cy < PICK_Y_THRESHOLD` (start when `cy > APPROACH_Y_MIN`).
  - PICKING: stop; arm down (`SERVO_ARM=5 → ARM_DOWN`), gripper close (`6 → GRIP_CLOSE`), arm up.
  - PLACING (service `place` or chassis stopped for N checks after pick): arm down, gripper open, arm up.
- Constant values (`ARM_DOWN`, `GRIP_CLOSE`, `PICK_Y_THRESHOLD`, …) are not on the wiki – read them from the source.

---

## 7. Chapter 5: MediaPipe human-robot interaction

**The page returns HTTP 404** (checked directly and via the sidebar link). Only the section titles are known:
5.1 MediaPipe intro, background segmentation, 3D object detection (Objectron), face tracking, 3D face detection
(face mesh), hand keypoint detection, human-robot interactive grasping, body keypoint detection,
fingertip distance recognition, body gesture control, human pose detection.
Get the code from the robot (`~/ros2_ws/src/example/example/mediapipe*` or similar) before porting.

**Java options** – MediaPipe has official APIs for Python, Android, iOS and Web, but **not for desktop/ARM Java**:

| Option | How | Effort | Notes |
|---|---|---|---|
| 1. ONNX models + ONNX Runtime Java | Use ONNX conversions of the MediaPipe models (palm detection + hand landmark, BlazePose detector + landmark, BlazeFace + face mesh, selfie segmentation; e.g. from PINTO_model_zoo), write pre/post-processing yourself | High (anchors, ROI cropping, landmark decoding) | Pure Java, runs on Pi 5 CPU |
| 2. TFLite models via JNI | TFLite C API through JavaCPP/Panama | High | Same models as MediaPipe uses internally |
| 3. Python sidecar | Keep a small Python process with `mediapipe`, send landmarks to Java via ZeroMQ (`org.zeromq:jeromq`) / WebSocket / ROS topic | Low | Not "all Java", but fastest to working result |
| 4. OpenCV DNN | `Dnn.readNetFromONNX` for single-stage models (face detection: OpenCV's `FaceDetectorYN` / YuNet is built in) | Medium | `FaceDetectorYN` covers face tracking without MediaPipe |

Landmark logic is plain math and ports 1:1 regardless of how landmarks are produced:

```java
record Lm(double x, double y, double z) {}

/** angle at joint b between a-b and c-b, degrees */
static double angle(Lm a, Lm b, Lm c) {
    double v1x = a.x()-b.x(), v1y = a.y()-b.y(), v2x = c.x()-b.x(), v2y = c.y()-b.y();
    double cos = (v1x*v2x + v1y*v2y) / (Math.hypot(v1x, v1y) * Math.hypot(v2x, v2y));
    return Math.toDegrees(Math.acos(Math.clamp(cos, -1, 1)));
}

/** MediaPipe hand: tips 4,8,12,16,20; PIP 3,6,10,14,18; MCP 2,5,9,13,17; wrist 0 */
static int countFingers(List<Lm> h) {
    int[][] f = { {2,3,4}, {5,6,8}, {9,10,12}, {13,14,16}, {17,18,20} };
    int n = 0;
    for (int[] j : f) if (angle(h.get(j[0]), h.get(j[1]), h.get(j[2])) > 150) n++;   // straight finger
    return n;
}

/** fingertip distance (thumb tip 4 ↔ index tip 8) in pixels */
static double pinch(List<Lm> h, int w, int hgt) {
    return Math.hypot((h.get(4).x()-h.get(8).x())*w, (h.get(4).y()-h.get(8).y())*hgt);
}
```

The angle thresholds and the gesture → motion mapping (which finger count drives which `cmdVel`) must come from
the original scripts.

---

## 8. Chapter 6: machine learning (YOLO26)

What the wiki shows [wiki / wiki-summary]:

1. Collect images: `~/software/collect_picture/main.py` → `~/my_data/JPEGImages`.
2. Annotate with labelImg (Pascal VOC XML in `Annotations`), classes in `~/my_data/classes.names`.
3. Convert: `xml2yolo.py --data ~/my_data --yaml ~/my_data/data.yaml` (YAML with `names`, `nc`, `train`, `val`).
4. Train: `python3 train.py --img 640 --batch 64 --epochs 300 --data ~/my_data/data.yaml --weights yolo26n.pt`
   (GPU PC recommended) → `runs/detect/trainN/weights/best.pt`.
5. Run: `ros2 launch example yolo_detect_demo.launch.py model_name:=best` – used for traffic-sign recognition
   (`best_traffic_26`) in autonomous driving.

**Java port:** steps 1–3 can be Java (OpenCV capture + `Imgcodecs.imwrite`, DOM parsing of VOC XML).
**Training stays in Python** – there is no practical Java YOLO trainer. Export the trained model once:

```bash
yolo export model=best.pt format=onnx imgsz=640        # or opset=12, simplify=True
```

Inference in Java with ONNX Runtime. YOLO26 is NMS-free end-to-end; the exported output is typically
`[1, N, 6]` = `x1, y1, x2, y2, score, class` [verify with Netron or `session.getOutputInfo()`].
Older YOLO (v8) exports `[1, 4+nc, 8400]` and needs NMS (`Dnn.NMSBoxes`).

```java
public final class YoloDetector implements AutoCloseable {
    private final OrtEnvironment env = OrtEnvironment.getEnvironment();
    private final OrtSession session;
    private final List<String> names;
    private final int size = 640;

    public YoloDetector(Path onnx, List<String> names) throws OrtException {
        this.session = env.createSession(onnx.toString(), new OrtSession.SessionOptions());
        this.names = names;
    }

    public record Detection(String label, float score, Rect2d box) {}

    public List<Detection> detect(Mat bgr, float minScore) throws OrtException {
        // letterbox
        double scale = Math.min((double) size / bgr.cols(), (double) size / bgr.rows());
        int nw = (int) (bgr.cols() * scale), nh = (int) (bgr.rows() * scale);
        int padX = (size - nw) / 2, padY = (size - nh) / 2;
        Mat resized = new Mat(), padded = new Mat(size, size, CvType.CV_8UC3, new Scalar(114, 114, 114));
        Imgproc.resize(bgr, resized, new Size(nw, nh));
        resized.copyTo(padded.submat(padY, padY + nh, padX, padX + nw));
        Imgproc.cvtColor(padded, padded, Imgproc.COLOR_BGR2RGB);

        // HWC uint8 → CHW float32 / 255
        byte[] px = new byte[size * size * 3];
        padded.get(0, 0, px);
        FloatBuffer chw = FloatBuffer.allocate(3 * size * size);
        for (int c = 0; c < 3; c++)
            for (int i = 0; i < size * size; i++) chw.put((px[i * 3 + c] & 0xFF) / 255f);
        chw.rewind();

        try (OnnxTensor input = OnnxTensor.createTensor(env, chw, new long[] {1, 3, size, size});
             OrtSession.Result out = session.run(Map.of(session.getInputNames().iterator().next(), input))) {
            float[][] rows = ((float[][][]) out.get(0).getValue())[0];   // [N][6]  – YOLO26 e2e
            List<Detection> res = new ArrayList<>();
            for (float[] r : rows) {
                if (r[4] < minScore) continue;
                double x1 = (r[0] - padX) / scale, y1 = (r[1] - padY) / scale;
                double x2 = (r[2] - padX) / scale, y2 = (r[3] - padY) / scale;
                res.add(new Detection(names.get((int) r[5]), r[4], new Rect2d(x1, y1, x2 - x1, y2 - y1)));
            }
            return res;
        }
    }

    @Override public void close() throws OrtException { session.close(); }
}
```

Alternatives: OpenCV `Dnn.readNetFromONNX` (no extra dependency, slower), DJL with the ONNX Runtime engine
(more abstraction, built-in translators for YOLOv5/v8). On the Pi 5 CPU expect a few FPS at 640 px;
export with `imgsz=320` or INT8 quantization for more speed.

---

## 9. Chapter 7: network configuration [wiki]

- AP mode (default): SSID must start with `HW`, password ≥ 8 chars; edit `hiwonder-toolbox/wifi_conf.py`,
  then `sudo systemctl restart hw_wifi.service`. Key names are only in screenshots.
- STA/LAN mode: via the "WiFi Tool PC" desktop app; success = LED2 solid on. Robot IP shown in WonderPi
  (long press on the robot icon).

This is OS configuration, not robot logic – **keep the Hiwonder tooling**. If Java must do it, run
`nmcli device wifi connect <ssid> password <pw>` / `nmcli device wifi hotspot …` via `ProcessBuilder`,
or use NetworkManager over D-Bus (`com.github.hypfvieh:dbus-java-core`).

---

## 10. Chapter 8: large AI models, voice

### 10.1 WonderEcho Pro wake word [wiki]

USB serial device (`/dev/ttyUSB0` or `/dev/ttyCH341USB1`), 115200 baud. When the wake word is detected the
module sends **`AA 55 03 00 FB`**. It is also a USB sound card for mic and speaker.

```java
public final class WonderEchoPro implements AutoCloseable {
    private static final byte[] WAKE = { (byte)0xAA, 0x55, 0x03, 0x00, (byte)0xFB };
    private final SerialPort port;
    private final byte[] window = new byte[WAKE.length];

    public WonderEchoPro(String dev) {
        port = SerialPort.getCommPort(dev);
        port.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 20, 0);
        if (!port.openPort()) throw new IllegalStateException(dev);
    }

    /** non-blocking poll – like kws.wakeup() in a 20 ms loop */
    public boolean wakeup() {
        byte[] b = new byte[1];
        while (port.bytesAvailable() > 0 && port.readBytes(b, 1) == 1) {
            System.arraycopy(window, 1, window, 0, window.length - 1);
            window[window.length - 1] = b[0];
            if (Arrays.equals(window, WAKE)) return true;
        }
        return false;
    }

    @Override public void close() { port.closePort(); }
}
```

Python's `wakeup_demo.py` switches the Pi 5 fan off while listening (`pinctrl FAN_PWM op dh`) and back to
auto on exit (`pinctrl FAN_PWM a0`) – do the same with `ProcessBuilder` in a `try/finally`.

### 10.2 Audio I/O (`pyaudio`, `arecord`, `aplay`)

Format used: 16 kHz, 16-bit signed LE (the wiki records 2 channels for the test; ASR works best with mono).

```java
AudioFormat fmt = new AudioFormat(16000f, 16, 1, true, false);

byte[] record(Duration d) throws LineUnavailableException {
    try (TargetDataLine mic = AudioSystem.getTargetDataLine(fmt)) {   // pick the WonderEcho mixer if needed:
        mic.open(fmt); mic.start();                                    // AudioSystem.getMixerInfo()
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[3200];
        long end = System.nanoTime() + d.toNanos();
        while (System.nanoTime() < end) out.write(buf, 0, mic.read(buf, 0, buf.length));
        return out.toByteArray();
    }
}

void playWav(Path wav) throws Exception {
    try (AudioInputStream in = AudioSystem.getAudioInputStream(wav.toFile());
         SourceDataLine line = AudioSystem.getSourceDataLine(in.getFormat())) {
        line.open(in.getFormat()); line.start();
        in.transferTo(new OutputStream() {
            public void write(int b) { line.write(new byte[]{(byte) b}, 0, 1); }
            public void write(byte[] b, int o, int l) { line.write(b, o, l); }
        });
        line.drain();
    }
}
```

Simple voice activity detection (replaces the Realtime-API VAD with `threshold=0.2`, `prefix_padding_ms=300`,
`silence_duration_ms=800`): compute RMS per 20 ms chunk, start recording above a threshold, keep 300 ms
before, stop after 800 ms below threshold.

### 10.3 OpenAI LLM / ASR / TTS (`openai`, `speech.RealTimeOpenAIASR`, `RealTimeOpenAITTS`)

Models from the wiki: LLM `gpt-4o-mini`; ASR `whisper-1` (or `gpt-4o-transcribe`);
TTS `gpt-4o-mini-tts` (or `tts-1`, `tts-1-hd`), voices `nova, shimmer, echo, onyx, fable, alloy, ash, sage, coral`.
Keys in `~/large_models/config.py`: `llm_api_key` (OpenAI), `vllm_api_key` (OpenRouter).
In Java put them in environment variables or a `.properties` file outside git.

With **openai-java** (builder API; check method names against the version you use):

```java
OpenAIClient openai = OpenAIOkHttpClient.builder().apiKey(System.getenv("OPENAI_API_KEY")).build();

// ASR
String transcribe(Path wav) {
    TranscriptionCreateParams p = TranscriptionCreateParams.builder()
        .file(wav).model(AudioModel.WHISPER_1).language("en").build();
    return openai.audio().transcriptions().create(p).asTranscription().text();
}

// LLM
String chat(String system, String user) {
    ChatCompletionCreateParams p = ChatCompletionCreateParams.builder()
        .model("gpt-4o-mini").addSystemMessage(system).addUserMessage(user).build();
    return openai.chat().completions().create(p).choices().get(0).message().content().orElse("");
}

// TTS → wav
void speak(String text, Path out) throws IOException {
    SpeechCreateParams p = SpeechCreateParams.builder()
        .model("gpt-4o-mini-tts").voice(SpeechCreateParams.Voice.ONYX).input(text)
        .responseFormat(SpeechCreateParams.ResponseFormat.WAV).speed(1.0).build();
    try (HttpResponse resp = openai.audio().speech().create(p)) {        // com.openai.core.http.HttpResponse Files.write(out, resp.body().readAllBytes()); }
}
```

OpenRouter and DashScope (Qwen) are OpenAI-compatible: same client with `.baseUrl("https://openrouter.ai/api/v1")`
or DashScope's compatible-mode URL and the matching key. If you prefer a framework: **LangChain4j** or
**Spring AI** wrap the same calls and add tool calling.

Basic demos (`openai_llm_demo.py`, `openai_llm_nlu_demo.py`, `openai_llm_er_demo.py`) = one `chat(...)` call each
with a different prompt (write a 50-word article, summarize, classify emotion of "So Sad" / "Ha Ha").

Voice loop (8.2): wake word → play `wakeup.wav` → record with VAD → ASR → LLM → TTS → play;
if nothing heard, play `no_voice.wav`.

### 10.4 LLM → robot actions (8.3, `llm_control_move`, `llm_color_track`, `llm_line_following`)

The LLM returns JSON like `{"action": ["forward", "color_track('green')"], "response": "Got it!"}`
(response 10–30 chars, JSON only) [wiki]. Actions are plain strings, not function-calling schemas.

Action vocabulary from the prompts: `forward` (1 s), `back`, `turn_left`, `turn_right`, `left_move`, `right_move`,
`drift`, `nod`, `shake_head`, RGB `(255,0,0)`, `color_track('red')`, `line_following('black')` [wiki-summary].

```java
public final class ActionDispatcher {
    private static final Pattern CALL = Pattern.compile("(\\w+)\\('?(\\w*)'?\\)");
    private final RobotIo io; private final Tracker tracker; private final LineFollower line;
    private volatile boolean stop;                     // set by wake-word interrupt

    public void handle(String llmJson) throws Exception {
        JsonNode n = new ObjectMapper().readTree(stripCodeFence(llmJson));
        tts.speakAsync(n.path("response").asText());
        for (JsonNode a : n.path("action")) {
            if (stop) break;
            String act = a.asText();
            switch (act) {
                case "forward"    -> pulse(0.3, 0, 0);
                case "back"       -> pulse(-0.5, 0, 0);
                case "left_move"  -> pulse(0, 0.5, 0);
                case "right_move" -> pulse(0, -0.5, 0);
                case "turn_left"  -> pulse(0, 0, 5);
                case "turn_right" -> pulse(0, 0, -5);
                case "drift"      -> pulse(0.5, 0, 4);
                case "nod"        -> head.nod();
                case "shake_head" -> head.shake();
                default -> {
                    Matcher m = CALL.matcher(act);
                    if (m.matches() && m.group(1).equals("color_track"))    tracker.start(m.group(2));  // stops after 5 s lost
                    else if (m.matches() && m.group(1).equals("line_following")) line.start(m.group(2));
                    else if (act.startsWith("(")) setRgbFromTuple(act);
                    // unknown → ignore (as in the original)
                }
            }
        }
    }
    private void pulse(double x, double y, double z) throws InterruptedException {
        io.cmdVel(x, y, z); Thread.sleep(1000); io.stop();
    }
}
```

Use OpenAI structured output (`response_format: json_schema`) in Java to make the JSON reliable; the
original only relies on the prompt.

### 10.5 Embodied AI with vision (8.4)

| Demo | Logic | Java |
|---|---|---|
| `vllm_with_camera` | Take latest camera frame (queue of 2), send image + question to a vision LLM, speak answer | `Imgcodecs.imencode(".jpg", mat, buf)` → base64 data URL → chat message with image content part |
| `vllm_smart_housekeeper` | VLM returns `{type, object, action[], response}`; `"alert()"` plays an alert sound and is removed from the list | Same dispatcher + `playWav(alert)` |
| `vllm_obstacle_avoidance` | VLM returns actions `move_forward()`, `turn_left()`, `move_backward()`, `turn_right()`, `move_left()`, `move_right()`, `move_forward_fast()`, `move_forward_slow()`, `buzzer()` plus `distance_one` (mm); `move_forward` runs until sonar ≤ `distance_one` | Dispatcher + loop on `io.sonarDistanceMm()` |

Original ROS wiring (only needed if you keep ROS): topics `tts_node/tts_text`, `agent_process/result`,
`tts_node/play_finish`, `vocal_detect/wakeup`; services `agent_process/set_model` (`SetModel{model, model_type,
api_key, base_url}`), `agent_process/set_prompt`, `vocal_detect/enable_wakeup`, `vocal_detect/set_mode`.
In a Java port these become plain method calls inside one process. The topics can stay as jros2 topics if you
want to mix Java and the original Python nodes; the services cannot (jros2 has no services yet).

---

## 11. Chapter 9: OpenClaw

OpenClaw is a self-hosted AI agent gateway (Node.js/TypeScript, not Python): a gateway service, a TUI,
chat channels (Telegram), a workspace with `IDENTITY.md`, `MEMORY.md` and `skills/` [wiki: commands and files;
technology: verify]. In the course it runs on a VM; the robot exposes ROS 2 services for "smart logistics":

| Service | Type | Values |
|---|---|---|
| `/smart_logistics/get_location` | `std_srvs/srv/Trigger` | – |
| `/smart_logistics/execute` | `interfaces/srv/SetString` | `'A'`, `'B'`, `'C'` (go to area) |
| `/smart_logistics/pick` | `interfaces/srv/SetString` | `'red'`, `'green'`, `'blue'` |
| `/smart_logistics/place` | `std_srvs/srv/Trigger` | – |

Robot side: `ros2 launch example smart_logistics.launch.py` (options `use_tts`, `use_large_models_tts`).
Camera skill: `skills/ros2-cam-subscribe/scripts/cam_subscribe.sh`.

**Java port:** there is no reason to rewrite OpenClaw itself. Port the *robot side*:

1. Implement `SmartLogistics` in Java (navigation to A/B/C via line following + crossings from 6.9/6.10,
   pick/place from 6.11, inventory memory).
2. Expose it in a way OpenClaw skills can call (jros2 cannot *serve* ROS 2 services yet):
   - keep the same ROS 2 service names via a small `rclpy` shim node that forwards to Java over topics
     (§4.3.4), so existing skills work unchanged; or
   - a small REST API (Javalin / Spring Boot): `POST /logistics/execute {"area":"C"}`, `POST /pick {"color":"red"}`,
     `POST /place`, `GET /location`, `GET /camera.jpg`, and skills that call it with `curl`; or
   - an **MCP server in Java** (MCP Java SDK, `io.modelcontextprotocol.sdk:mcp`) with tools
     `go_to_area`, `pick`, `place`, `get_location`, `take_photo` – usable by OpenClaw and any MCP-capable agent.

---

## 12. Open points to resolve on the robot

1. Copy all sources from the container (see §0) – most constants (ROI, PID gains, servo positions,
   thresholds) are only there.
2. `ros2 topic list -t`, `ros2 service list -t`, `ros2 interface show …` for all custom message types.
3. Controller board protocol (port, baud, function codes) in `ros_robot_controller_sdk.py` – needed for option B.
4. I2C addresses/registers in `sdk/sonar.py`, `sdk/FourInfrared.py`.
5. Line sensor polarity (1 over black, or inverted?).
6. Mecanum rotation signs and motor mapping A–D → M1–M4 (option B).
7. Chapter 5 (MediaPipe) – page is 404; get the scripts from the robot or ask Hiwonder.
8. Section 4.6 (QR) – action mapping from `app/qrcode.py`.
9. YOLO26 ONNX output shape after export.
10. Copy the custom message packages (`ros_robot_controller_msgs`, `servo_controller_msgs`) and check the
    generated accessor names; check the container's `RMW_IMPLEMENTATION`, `ROS_DOMAIN_ID` and network mode.
11. Whether a newer jros2 release adds services/actions – then §4.3.4 workarounds can go.

## 13. Suggested implementation order

1. Maven setup + one-off message generation (§3), `RobotIo` + `Jros2RobotIo` + `FakeRobotIo`; chapter 3 demos
   (proves DDS discovery and the chain work: `ros2 topic echo /cmd_vel` in the container).
2. Vision utilities + lessons 4.1, 4.2, 4.7 (simple, cover camera, servos, buzzer, RGB, sonar).
3. Lessons 4.4, 4.5, 4.8 (PID and line following), then 4.9/4.10 (state machines, arm).
4. YOLO ONNX inference (chapter 6) with the model trained in Python.
5. Voice: WonderEcho wake word, audio I/O, OpenAI ASR/LLM/TTS, action dispatcher (chapter 8).
6. Vision LLM demos (8.4), smart logistics + MCP/REST for OpenClaw (chapter 9).
7. MediaPipe features once the sources are available (sidecar first, ONNX later).
8. Optionally: `DirectRobotIo` (option B) to drop ROS.
