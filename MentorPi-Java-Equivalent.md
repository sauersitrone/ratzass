# MentorPi (Hiwonder) – Java Equivalent of the Python/ROS 2 Course

Reference for porting the software functions of the Hiwonder **MentorPi** (Raspberry Pi 5, ROS 2 Humble,
mecanum chassis **M1** or Ackermann chassis **A1**, RRC Lite STM32 controller, Oradar MS200 TOF lidar,
`ascamera` 3D depth camera or USB monocular camera on a pan-tilt, WonderEcho Pro voice box) from Python/ROS 2
to **Java 21**, using **Maven** and **IHMC jros2** for ROS 2.

Source: Hiwonder wiki "MentorPi", chapters 1–14 + appendix
(`https://wiki.hiwonder.com/projects/MentorPi/en/latest/docs/…`), read on 2026-11-27, plus the source archives
`controller.zip` and `app.zip` linked from the wiki (read in full for §6 and §7, §11.4).

Companion document: `TurboPi-Java-Equivalent.md` (same approach, same library choices).

---

## 0. Sources, coverage, markers

| Ch. | Wiki page | Coverage |
|---|---|---|
| 1 | Getting Ready | read (sections 1.3–1.7 only as summary) |
| 2 | Set Development Environment | read in full |
| 3 | Motion Control | read in full **+ source `controller.zip`** |
| 4 | Lidar | read in full **+ source `app.zip`** |
| 5 | Depth Camera | read in full |
| 6 | Mapping | read in full |
| 7 | Navigation | read in full (no `nav2_params.yaml` content on the page) |
| 8 | ROS + OpenCV | read in full **+ `app/line_following.py`, `app/object_tracking.py`** |
| 9 | MediaPipe | read in full |
| 10 | Machine Learning (YOLOv5) | first 2/3 verbatim, rest summary |
| 11 | Autonomous Driving | first 5/6 verbatim, rest summary |
| 12 | Master-Slave / Group Control | read in full |
| 13 | Network | read in full |
| 14 | Large AI Models | read in full |

Markers:

- **[wiki]** – stated on the wiki page. **[src]** – taken from the downloaded Python source.
- **[wiki-summary]** – from a part only available as a summary; verify.
- **[verify]** – not on the wiki/source read; general knowledge of Hiwonder/ROS/libraries. Check on the robot.

**The source code is public.** Unlike TurboPi, the MentorPi wiki links zip archives of most packages:

```
https://wiki.hiwonder.com/projects/MentorPi/en/latest/_static/source_code/<name>.zip
```

Names referenced on the pages: `controller`, `app` (both downloaded and checked), `color_detect`, `qrcode`,
`color_track`, `hand_track`, `mediapipe_example`, `body_control`, `self_driving`, `yolov5_trt`, `yolov5_detect`,
`slam`, `navigation`, `multi`, `large_models_sdk`. Download them before porting a chapter – they contain the
constants the pages leave out. The complete system image is only available from Hiwonder support with an order
number [wiki].

Wiki inconsistencies that matter are listed per chapter (the pages contain several copy/paste errors, e.g.
`~/.stop_sh` vs `~/.stop_ros.sh`, JetRover text in MentorPi pages).

---

## 1. Architecture: what to port, what to keep

MentorPi is a full ROS 2 Humble stack in Docker. Some parts are heavy C++ libraries that have no sensible Java
replacement; the application logic around them ports well.

```
┌──────────────────── Java 21 app (your code, one process) ─────────────────────┐
│ chassis controller (kinematics + odom_raw)  · lidar games · OpenCV games       │
│ line following · tracking · autonomous driving · YOLO (ONNX) · MediaPipe logic │
│ navigation goals · multi-robot · LLM / ASR / TTS / voice actions               │
└───────────────┬───────────────────────────────────────────────────────────────┘
                │ jros2 (IHMC) – ROS 2 topics over DDS (no services/actions yet)
┌───────────────▼───────────────────────────────────────────────────────────────┐
│ keep (C++/vendor): ros_robot_controller (STM32 board), MS200 lidar driver,     │
│ ascamera / usb_cam driver, imu_filter + imu_calib, robot_localization EKF,     │
│ robot_state_publisher, slam_toolbox, RTAB-Map, Nav2                             │
└───────────────┬───────────────────────────────────────────────────────────────┘
                │ UART
        RRC Lite STM32 board (motors with encoders, PWM servos, IMU, buzzer, RGB, key)
```

| Python component | Port to Java? | Why |
|---|---|---|
| `controller/odom_publisher_node.py` (kinematics, `odom_raw`) | **yes** (§6) | Pure math + topics |
| `app/lidar_controller.py`, `line_following.py`, `object_tracking.py` | **yes** (§7, §11) | Logic + OpenCV |
| `example/*` (color, QR, tracking, MediaPipe, body control, self driving) | **yes** | Logic + OpenCV/ONNX |
| `large_models/*` (voice, LLM, VLM agents) | **yes** (§17) | HTTP APIs + logic |
| `multi/*` (group control) | **yes** (§15) | Topics with namespaces |
| `ros_robot_controller` (board driver) | keep, optional option B (§4.5) | Serial protocol not on the wiki |
| lidar / camera drivers, `imu_filter`, `imu_calib`, EKF | keep | Vendor/C++ packages |
| `slam_toolbox`, RTAB-Map, Nav2, AMCL | **keep** | Large C++ frameworks; Java talks to them via topics (§9, §10) |

**jros2 has topics only** (services, actions and parameters are "soon" in its README). Consequences:

- Services between the course's own nodes (`~/enter`, `~/exit`, `~/set_running`, `~/set_param`,
  `~/set_target_color`, `agent_process/set_prompt`, …) become **plain method calls** inside the Java process.
- Starting/stopping games from outside: a **command topic** `/mentorpi_java/command` (`std_msgs/String`) (§4.4).
- Nav2 goals are sent as **topics** (`/goal_pose`, `/initialpose`) instead of the `navigate_to_pose` action (§10).
- Services of kept C++ nodes that you still need (`controller/load_calibrate_param`, Nav2 lifecycle, …):
  not needed in the normal flow; if required, a ~30-line `rclpy` shim node.

---

## 2. Python → Java library mapping

| Python | Purpose | Java | Maven coordinates |
|---|---|---|---|
| `rclpy`, launch files | Nodes, topics | **jros2** `ROS2Node`, `ROS2Topic`, `ROS2Publisher`, `createSubscription`; launch → `main()` | `us.ihmc:jros2:1.5.1` (IHMC repo) |
| std messages (`geometry_msgs`, `sensor_msgs`, `nav_msgs`, `std_msgs`) | Message classes | bundled in jros2 as `<pkg>.<Type>` (`geometry_msgs.Twist`, `sensor_msgs.LaserScan`, `nav_msgs.Odometry`, `std_msgs.String_`) [verify `nav_msgs` is bundled] | jros2 |
| `ros_robot_controller_msgs`, `interfaces` (msg part) | Custom messages | jros2 generator, run once (§3.2) | `us.ihmc.jros2.generator` |
| `cv2` | Image processing | OpenCV Java (same names) | `org.openpnp:opencv:4.9.0-0` |
| `cv_bridge` | Image ↔ Mat | own converter (§4.3) | – |
| `numpy` | arrays, `nonzero`, `isfinite` | plain `float[]` loops (§7), `Mat` ops | – |
| `pid.PID` (Hiwonder) | PID | own class (§4.2) | – |
| `common.set_range`, `val_map` | clamp, map | `Math.clamp`, own `map` | JDK 21 |
| `yaml` / `get_yaml_data` | LAB thresholds | SnakeYAML / Jackson YAML | `org.yaml:snakeyaml:2.2` |
| `threading`, `queue`, `RLock` | concurrency | `java.util.concurrent`, `ReentrantLock` | JDK |
| `qrcode` (generate) | QR images | ZXing `QRCodeWriter` | `com.google.zxing:core:3.5.3`, `javase:3.5.3` |
| `pyzbar` (decode) | QR decoding | ZXing `MultiFormatReader` or OpenCV `QRCodeDetector` | same |
| `mediapipe` (hands, pose) | Landmarks | no official Java desktop/ARM API → ONNX models with ONNX Runtime, or Python sidecar (§12) | `com.microsoft.onnxruntime:onnxruntime:1.18.0` |
| YOLOv5 (`train.py`, `.pt`, TensorRT) | Detection | train in Python, export ONNX, infer with ONNX Runtime Java (§13) | ONNX Runtime |
| `nav2_map_server map_saver_cli` | Save map | own OccupancyGrid → PGM/YAML writer (§9.3), or keep the CLI | JDK |
| `openai`, `speech` (`RealTimeOpenAIASR/TTS`) | LLM, ASR, TTS | openai-java (§17) | `com.openai:openai-java` [verify version] |
| OpenRouter / StepFun / DashScope | VLM | OpenAI-compatible → openai-java with `baseUrl` | – |
| `speech.awake.WonderEchoPro` | Wake word over USB serial | jSerialComm (§17.1) | `com.fazecast:jSerialComm:2.10.4` |
| `pyaudio`, `arecord/aplay` | audio | `javax.sound.sampled` | JDK |
| `json` / `eval` of LLM output | parse | Jackson with lenient settings (**never** port `eval`) | `jackson-databind` |
| `rqt_image_view`, web monitor `:8080` | view images | MJPEG server with `com.sun.net.httpserver` (§8.4) | JDK |

---

## 3. Project setup (Maven)

Same structure as the TurboPi document: Maven build, custom message classes generated **once** with jros2's
Gradle plugin into `src/main/generated-ros/` and committed.

```
mentorpi-java/
├── pom.xml
├── src/main/java/…
├── src/main/generated-ros/…           generated message classes (committed)
└── tools/ros-msg-gen/                 one-off generator (Gradle)
    └── ros_interfaces/                copied from the robot: ros_robot_controller_msgs/, interfaces/
```

### 3.1 `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>dev.mentorpi</groupId>
  <artifactId>mentorpi-java</artifactId>
  <version>0.1.0</version>

  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <jros2.version>1.5.1</jros2.version>
    <openai.version><!-- current release from Maven Central --></openai.version>
  </properties>

  <repositories>
    <repository>
      <id>ihmc-repo</id>
      <url>https://robotlabfiles.ihmc.us/repository/</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency><groupId>us.ihmc</groupId><artifactId>jros2</artifactId><version>${jros2.version}</version></dependency>

    <dependency><groupId>org.openpnp</groupId><artifactId>opencv</artifactId><version>4.9.0-0</version></dependency>
    <dependency><groupId>com.google.zxing</groupId><artifactId>core</artifactId><version>3.5.3</version></dependency>
    <dependency><groupId>com.google.zxing</groupId><artifactId>javase</artifactId><version>3.5.3</version></dependency>
    <dependency><groupId>com.microsoft.onnxruntime</groupId><artifactId>onnxruntime</artifactId><version>1.18.0</version></dependency>

    <dependency><groupId>com.fazecast</groupId><artifactId>jSerialComm</artifactId><version>2.10.4</version></dependency>

    <dependency><groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-databind</artifactId><version>2.17.2</version></dependency>
    <dependency><groupId>com.fasterxml.jackson.dataformat</groupId><artifactId>jackson-dataformat-yaml</artifactId><version>2.17.2</version></dependency>

    <dependency><groupId>com.openai</groupId><artifactId>openai-java</artifactId><version>${openai.version}</version></dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>build-helper-maven-plugin</artifactId>
        <version>3.6.0</version>
        <executions>
          <execution>
            <id>add-ros-messages</id>
            <phase>generate-sources</phase>
            <goals><goal>add-source</goal></goals>
            <configuration><sources><source>src/main/generated-ros</source></sources></configuration>
          </execution>
        </executions>
      </plugin>
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
                  <mainClass>dev.mentorpi.Main</mainClass>
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

Maven Central only has jros2 1.0.1; 1.5.1 (named in the jros2 README) comes from IHMC's repository – check with
`mvn dependency:resolve`. Run on the Pi 5 with an ARM64 JDK 21; check the shaded jar keeps jros2's native
Fast-DDS libraries (`jar tf target/*.jar | grep -i fastdds`).

### 3.2 One-off message generation (`tools/ros-msg-gen/`)

Copy the interface packages from the container (`ros2_ws/src/driver/ros_robot_controller_msgs`,
`ros2_ws/src/interfaces` – locate with `find ~/ros2_ws/src -name package.xml | xargs grep -l "<name>.*msgs\|<name>interfaces"`).
jros2 generates **`.msg` only**; `.srv` files (most of `interfaces`, all of `large_models_msgs`) are skipped
and not needed (§1).

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
        "ros_interfaces/ros_robot_controller_msgs",   // MotorsState, MotorState, SetPWMServoState, PWMServoState, BuzzerState, RGBStates, …
        "ros_interfaces/interfaces",                  // Point2D, ColorDetect, … (msg folder only)
    ).map { projectDir.resolve(it).absolutePath }
    outputDir = projectDir.resolve("../../src/main/generated-ros").absolutePath
    cleanOutputDir = true
}
```

```bash
cd tools/ros-msg-gen && ./gradlew generateRobotMessages
cd ../.. && mvn package
```

---

## 4. Common building blocks

### 4.1 Robot configuration (environment variables)

The Python nodes read their variant from environment variables set by the robot version tool [wiki][src]:

```java
public record RobotConfig(String machineType,      // MentorPi_Mecanum | MentorPi_Acker
                          String depthCameraType,  // ascamera | usb_cam
                          String lidarType,        // MS200 (default); code special-cases "G4"
                          String asrLanguage,      // "Chinese" or English
                          String master, String host) {

    public static RobotConfig fromEnv() {
        return new RobotConfig(
            System.getenv().getOrDefault("MACHINE_TYPE", "MentorPi_Mecanum"),
            System.getenv().getOrDefault("DEPTH_CAMERA_TYPE", "ascamera"),
            System.getenv().getOrDefault("LIDAR_TYPE", "MS200"),
            System.getenv().getOrDefault("ASR_LANGUAGE", "English"),
            System.getenv().getOrDefault("MASTER", "/"),
            System.getenv().getOrDefault("HOST", "/"));
    }

    public boolean ackermann() { return "MentorPi_Acker".equals(machineType); }
    public boolean stereo()    { return "ascamera".equals(depthCameraType); }
    public String labKey()     { return stereo() ? "Stereo" : "Mono"; }
}
```

Run the Java app inside the container (`source ~/ros2_ws/.typerc` sets these variables) or copy the values.

### 4.2 Hiwonder `pid.PID`

Used as `PID(kp, ki, kd)`, `.SetPoint`, `.update(feedback)`, `.output`, `.clear()`; error = `SetPoint − feedback`,
default set point 0 [src usage].

```java
public final class Pid {
    private final double kp, ki, kd;
    private double setPoint, iTerm, lastError, output;
    private long lastTime = System.nanoTime();

    public Pid(double kp, double ki, double kd) { this.kp = kp; this.ki = ki; this.kd = kd; }

    public void setPoint(double sp) { this.setPoint = sp; }
    public double output() { return output; }

    public double update(double feedback) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        double error = setPoint - feedback;
        iTerm = Math.clamp(iTerm + error * dt, -20.0, 20.0);          // windup guard [verify value in pid.py]
        double dTerm = dt > 0 ? (error - lastError) / dt : 0;
        lastTime = now; lastError = error;
        output = kp * error + ki * iTerm + kd * dTerm;
        return output;
    }

    public void clear() { iTerm = 0; lastError = 0; output = 0; setPoint = 0; lastTime = System.nanoTime(); }
}
```

### 4.3 jros2 node and the MentorPi topics

Topic names below are from the source [src] unless marked.

| Topic | Type | Direction (Java) | Used for |
|---|---|---|---|
| `/controller/cmd_vel` | `geometry_msgs/Twist` | pub | all games, navigation, LLM |
| `cmd_vel` | `geometry_msgs/Twist` | – | app input of the controller node (clamped to ±0.2 m/s, ±0.5 rad/s) |
| `ros_robot_controller/set_motor` | `ros_robot_controller_msgs/MotorsState` | pub (controller port) | wheel speeds in rps |
| `ros_robot_controller/pwm_servo/set_state` | `ros_robot_controller_msgs/SetPWMServoState` | pub | pan-tilt, Ackermann steering (servo 3) |
| `ros_robot_controller/set_buzzer` | `BuzzerState` | pub | buzzer [wiki] |
| `ros_robot_controller/set_rgb` | `RGBStates` | pub | RGB LEDs [wiki] |
| `ros_robot_controller/imu_raw` → `/imu` | `sensor_msgs/Imu` | sub | IMU (raw → filtered) [wiki] |
| `odom_raw` / `odom` | `nav_msgs/Odometry` | pub / sub | controller odom / EKF output |
| `set_odom` | `geometry_msgs/Pose2D` | sub (controller port) | reset odom |
| `/scan_raw` | `sensor_msgs/LaserScan` (best effort) | sub | lidar |
| `/ascamera/camera_publisher/rgb0/image` | `sensor_msgs/Image` (rgb8) | sub | depth camera colour |
| `/ascamera/camera_publisher/depth0/image_raw` | `sensor_msgs/Image` | sub | depth image [wiki] |
| `/ascamera/camera_publisher/depth0/points` | `sensor_msgs/PointCloud2` | sub | point cloud [wiki RViz path; verify] |
| `/vocal_detect/wakeup` | `std_msgs/Bool` | sub/pub | wake word |
| `/goal_pose`, `/initialpose`, `/map` | Nav2 / AMCL / map | pub / pub / sub | §9, §10 |

Check everything on the robot with `ros2 topic list -t` (MS200 and ascamera topic names depend on the driver
config).

**Naming:** jros2 uses the ROS package name as the Java package (`geometry_msgs`, `ros_robot_controller_msgs`, …).
These names are fixed by ROS and by the classes bundled in jros2, so they cannot be changed to camelCase. The code
examples import the classes (`import ros_robot_controller_msgs.SetPWMServoState;`) and use only the class names.
Topic names, YAML keys and LLM action strings keep their snake_case, because they must match the robot. Namespaces: with group control, all topics move below `/robot_1/…` (§15).

```java
import geometry_msgs.Twist;
import ros_robot_controller_msgs.BuzzerState;
import ros_robot_controller_msgs.PWMServoState;
import ros_robot_controller_msgs.SetPWMServoState;
import sensor_msgs.Image;
import sensor_msgs.LaserScan;

public final class MentorPiRos implements AutoCloseable {
    public final ROS2Node node;
    private final ROS2Publisher<Twist> cmdVelPub;
    private final ROS2Publisher<SetPWMServoState> servoPub;
    private final ROS2Publisher<BuzzerState> buzzerPub;
    private final Twist twist = new Twist();

    public MentorPiRos(String nodeName, int domainId, String ns) {   // ns = "" or "/robot_1"
        node = new ROS2Node(nodeName, domainId);                     // ROS_DOMAIN_ID is 0 on MentorPi [wiki]
        cmdVelPub = node.createPublisher(new ROS2Topic<>(ns + "/controller/cmd_vel", Twist.class));
        servoPub  = node.createPublisher(new ROS2Topic<>(ns + "/ros_robot_controller/pwm_servo/set_state",
                                                         SetPWMServoState.class));
        buzzerPub = node.createPublisher(new ROS2Topic<>(ns + "/ros_robot_controller/set_buzzer",
                                                         BuzzerState.class));
    }

    public void cmdVel(double vx, double vy, double wz) {
        synchronized (twist) {
            twist.getLinear().setX(vx); twist.getLinear().setY(vy); twist.getAngular().setZ(wz);   // accessors [verify]
            cmdVelPub.publish(twist);
        }
    }
    public void stop() { cmdVel(0, 0, 0); }

    /** PWMServoState has arrays: id[], position[] [src]; duration in seconds */
    public void servos(double durationSec, int[] ids, int[] pulses) {
        SetPWMServoState msg = new SetPWMServoState();
        msg.setDuration(durationSec);
        for (int i = 0; i < ids.length; i++) {
            PWMServoState s = msg.getState().add();
            s.getId().add(ids[i]);
            s.getPosition().add(pulses[i]);
        }
        servoPub.publish(msg);
    }

    public void buzzer(int freq, double onSec, double offSec, int repeat) {
        BuzzerState msg = new BuzzerState();
        msg.setFreq(freq); msg.setOnTime((float) onSec); msg.setOffTime((float) offSec); msg.setRepeat(repeat);
        buzzerPub.publish(msg);
    }

    public void onScan(Consumer<LaserScan> handler) {
        ROS2Topic<LaserScan> topic = new ROS2Topic<>("/scan_raw", LaserScan.class)
                .withQoS(ROS2QoSProfile.BEST_EFFORT);                // Python uses BEST_EFFORT, depth 1 [src]
        LaserScan reuse = new LaserScan();
        node.createSubscription(topic, reader -> { reader.read(reuse); handler.accept(reuse); });
    }

    public void onRgbImage(Consumer<Mat> handler) {
        ROS2Topic<Image> topic = new ROS2Topic<>("/ascamera/camera_publisher/rgb0/image",
                Image.class).withQoS(ROS2QoSProfile.BEST_EFFORT);
        Image reuse = new Image();
        node.createSubscription(topic, reader -> { reader.read(reuse); handler.accept(ImageConv.toBgr(reuse)); });
    }

    @Override public void close() { stop(); node.close(); }
}
```

Image conversion (`cv_bridge`), including 16-bit depth:

```java
import sensor_msgs.Image;

public final class ImageConv {
    /** rgb8 / bgr8 → BGR Mat (like imgmsg_to_cv2(msg, "bgr8")) */
    public static Mat toBgr(Image img) {
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        byte[] px = new byte[w * h * 3];
        ByteBuffer buf = img.getData().getBuffer();
        buf.rewind(); buf.get(px);
        Mat m = new Mat(h, w, CvType.CV_8UC3);
        m.put(0, 0, px);
        if ("rgb8".contentEquals(img.getEncoding())) Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2BGR);
        return m;
    }

    /** 16UC1 depth in millimetres → CV_16UC1 Mat */
    public static Mat toDepth(Image img) {
        int w = (int) img.getWidth(), h = (int) img.getHeight();
        ByteBuffer buf = img.getData().getBuffer().duplicate();
        buf.rewind();
        buf.order(img.getIsBigendian() != 0 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        short[] d = new short[w * h];
        buf.asShortBuffer().get(d);
        Mat m = new Mat(h, w, CvType.CV_16UC1);
        m.put(0, 0, d);
        return m;
    }
}
```

**Important colour-order note:** the Python nodes request `"rgb8"` from `cv_bridge` and then call
`COLOR_RGB2LAB` on it [src]. If your Java pipeline converts to BGR (as `toBgr` does), use `COLOR_BGR2LAB`.
Mixing them up silently swaps the a/b channels and breaks the LAB thresholds.

### 4.4 Replacing the game services: command topic

All Hiwonder games expose `~/enter`, `~/exit`, `~/set_running`, `~/set_param`, `~/set_target_color`,
`~/set_threshold`, `~/init_finish` [src]. In Java: one interface + one command topic.

```java
import std_msgs.String_;

public interface Game {
    void enter();
    void exit();
    void setRunning(boolean running);   // lidar game: setMode(int)
    default void command(String[] args) { }
}

/** ros2 topic pub --once /mentorpi_java/command std_msgs/msg/String "{data: 'lidar set_running 2'}" */
public final class CommandRouter {
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    public CommandRouter(ROS2Node node) {
        ROS2Topic<String_> topic = new ROS2Topic<>("/mentorpi_java/command", String_.class);
        node.createSubscription(topic, reader -> dispatch(reader.read().getData().toString().trim().split("\\s+")));
    }
    public void register(String name, Game g) { games.put(name, g); }

    private void dispatch(String[] a) {
        Game g = games.get(a[0]);
        if (g == null || a.length < 2) return;
        switch (a[1]) {
            case "enter"       -> g.enter();
            case "exit"        -> g.exit();
            case "set_running" -> g.setRunning(!a[2].equals("0") && !a[2].equalsIgnoreCase("false"));
            default            -> g.command(Arrays.copyOfRange(a, 1, a.length));
        }
    }
}
```

The `Heart` heartbeat (`~/heartbeat`, 5 s → exit) used by the app [src] is a watchdog for the phone app; in Java:
a `ScheduledExecutorService` that calls `exit()` when no heartbeat message arrived for 5 s.

### 4.5 Option B – talking to the RRC Lite board directly [verify]

Only needed if you want to drop `ros_robot_controller` too. The wiki does not document the board protocol.
Hiwonder's `ros_robot_controller_sdk.py` (in the container under `ros2_ws/src/driver/ros_robot_controller/`) is the
reference; the TurboPi document §4.4 contains a jSerialComm implementation of the commonly known frame format
(`0xAA 0x55 func len data crc8`, motor function sends `id, float rps`). Read the SDK file to confirm port, baud and
function codes before using it. IMU data and battery voltage also come from that board and would have to be parsed.

---

## 5. Chapters 1, 2, 13: hardware, environment, network

### 5.1 Hardware and constants [wiki][src]

| Item | Value |
|---|---|
| Computer | Raspberry Pi 5 |
| Controller | RRC Lite (STM32): 4 encoder motors, PWM servos, IMU, buzzer, LED1/LED2, KEY1 |
| Chassis M1 (mecanum) | wheelbase 0.1368 m, track 0.1446 m, wheel Ø 0.065 m [src] |
| Chassis A1 (Ackermann) | wheelbase 0.145 m, track 0.133 m, wheel Ø 0.067 m, steering servo **ID 3** [src] |
| Lidar | Oradar MS200 TOF, 360°, 0.8° resolution (≈450 points), 7–15 Hz, 0.1–13.4 m, UART |
| Camera | `ascamera` 3D depth camera, or USB monocular `usb_cam` on 2-DOF pan-tilt (servo 1 tilt, servo 2 pan) |
| Battery | LiPo 2S, charge below 6.4 V, charger 8.4 V 2 A [wiki-summary] |
| Voice | WonderEcho Pro (CI1302, USB audio + CH340 serial) |

Motor layout (mecanum, from code comment) [src]: motor 1 front-left, motor 2 rear-left, motor 3 front-right,
motor 4 rear-right.

### 5.2 Environment [wiki]

- Docker container (name not given; enter with `docker exec -it -u ubuntu -w /home/ubuntu <id> /bin/bash`).
- Workspace `/home/ubuntu/ros2_ws/src/`: `app`, `example`, `bringup`, `driver`, `interfaces`, `peripherals`,
  `navigation`, `slam`, `yolov5_ros2`, `simulations`, `large_models`, `multi`.
- Env file: `/home/ubuntu/ros2_ws/.typerc` (sourced from `~/.zshrc`). `ROS_DOMAIN_ID` 0.
- Auto-start: stop with `~/.stop_ros.sh`, restore with `sudo systemctl restart start_node.service` (on the host).
- Version tool sets camera type (`ascamera` / `usb_cam`) and machine type (`MentorPi_Mecanum` / `MentorPi_Acker`).
- Servo deviation: `./Servo.sh` GUI, "Save Offsets" (file path only in screenshots) – keep using it; the
  controller applies the offsets.
- Access: AP mode `192.168.149.1`, VNC user `pi`, password `raspberrypi`.

**Java deployment:** run the jar **inside the container** (simplest: same network namespace, same env vars,
same DDS settings) or on the host with the container on `--network host`. The container's RMW must be DDS-based
(`echo $RMW_IMPLEMENTATION`).

### 5.3 Network (chapter 13) [wiki]

`~/hiwonder-toolbox/wifi_conf.py` keys: `HW_WIFI_MODE` (1 = AP direct, 2 = LAN/STA, 3 = direct sharing),
`HW_WIFI_AP_SSID`, `HW_WIFI_AP_PASSWORD`, `HW_WIFI_STA_SSID`, `HW_WIFI_STA_PASSWORD`; apply with
`sudo systemctl restart wifi.service`; KEY1 long press returns to AP mode. All lines commented = AP mode.
This is OS configuration – keep it. If Java must change it: rewrite the file (it is plain `KEY = "value"` Python)
and run `systemctl` via `ProcessBuilder`.

---

## 6. Chapter 3: motion control – port of the controller node

`odom_publisher_node.py` [src] is small and pure logic: it subscribes to velocity commands, converts them to
wheel speeds (and a steering servo pulse) and integrates `odom_raw`. Everything else (board driver, IMU filter,
EKF) stays.

### 6.1 Mecanum kinematics (`mecanum.py`) [src]

```java
public final class MecanumChassis {
    private final double wheelbase, trackWidth, wheelDiameter;

    public MecanumChassis(double wheelbase, double trackWidth, double wheelDiameter) {
        this.wheelbase = wheelbase; this.trackWidth = trackWidth; this.wheelDiameter = wheelDiameter;
    }

    /** m/s → wheel revolutions per second */
    private double rps(double v) { return v / (Math.PI * wheelDiameter); }

    /** @return rps for motor ids 1..4 */
    public double[] setVelocity(double vx, double vy, double wz) {
        double k = (wheelbase + trackWidth) / 2;
        double m1 = vx - vy - wz * k;
        double m2 = vx + vy - wz * k;
        double m3 = vx + vy + wz * k;
        double m4 = vx - vy + wz * k;
        return new double[] { rps(-m1), rps(-m2), rps(m3), rps(m4) };   // left motors mounted mirrored
    }
}
```

### 6.2 Ackermann kinematics (`ackermann.py`) [src]

```java
public final class AckermannChassis {
    public record Command(Integer servoPulse, double[] rps) {}       // servoPulse null = do not move the servo

    private final double wheelbase, trackWidth, wheelDiameter;
    private final double maxSteer = Math.toRadians(45);              // source: 45°; wiki text says 29° or 36°

    public AckermannChassis(double wheelbase, double trackWidth, double wheelDiameter) {
        this.wheelbase = wheelbase; this.trackWidth = trackWidth; this.wheelDiameter = wheelDiameter;
    }
    private double rps(double v) { return v / (Math.PI * wheelDiameter); }

    public Command setVelocity(double v, double w) {
        if (Math.abs(v) < 1e-8) return new Command(null, new double[4]);
        int pulse = 1500;
        if (Math.abs(w) >= 1e-8) {
            double steer = Math.atan(wheelbase * w / v);
            // Python: if abs(steer) > 45°: steer = +45° (loses the sign – a bug); fixed here:
            steer = Math.copySign(Math.min(Math.abs(steer), maxSteer), steer);
            pulse = (int) (1500 + 2000 * Math.toDegrees(steer) / 180);
        }
        double vr = v + w * trackWidth / 2;
        double vl = v - w * trackWidth / 2;
        return new Command(pulse, new double[] { 0, rps(vl), 0, rps(-vr) });   // only motors 2 and 4 drive
    }
}
```

The wiki's Ackermann text uses `1500 + 2000·deg(−θ)/180` and 29°; the source uses `+θ` and 45°. Test the steering
direction on the robot (positive `angular.z` must turn left).

### 6.3 Odometry node (`odom_publisher`) in Java [src]

Behaviour of the original:

- Subscribes `controller/cmd_vel` (direct) and `cmd_vel` (app; clamped to ±0.2 m/s linear, ±0.5 rad/s angular).
- Mecanum: publish `MotorsState`. Ackermann: publish `MotorsState` + `SetPWMServoState` (servo **3**, duration 0.02 s).
- **Odometry is open-loop**: integrated from the *commanded* velocity at 50 Hz, not from encoders:
  `x += vx·dt·cos(yaw)`, `y += vx·dt·sin(yaw)`, `yaw += wz·dt`; position × `linear_correction_factor`.
  Lateral `vy` is published in the twist but **not integrated** into the pose (mecanum sideways motion is
  invisible to `odom_raw`; the EKF with IMU corrects yaw only).
- Covariance: moving → diag(1e-3, 1e-3, 1e6, 1e6, 1e6, 1e3); stopped → diag(1e-9, 1e-3, 1e6, 1e6, 1e6, 1e-9)
  with element [1][2] = 1e-9.
- Frames `odom` → `base_footprint`; no TF broadcast (the EKF publishes TF and remaps `odometry/filtered` → `odom`).
- `set_odom` (`Pose2D`) resets the pose and publishes `set_pose` (`PoseWithCovarianceStamped`) for the EKF.
- Calibration factors from `~/ros2_ws/src/driver/controller/config/calibrate_params.yaml`:
  `linear_correction_factor` (default 1.0), `angular_correction_factor` (1.0; declared but **not applied** in
  the shown code).

```java
import geometry_msgs.Pose2D;
import geometry_msgs.Twist;
import nav_msgs.Odometry;
import ros_robot_controller_msgs.MotorState;
import ros_robot_controller_msgs.MotorsState;
import ros_robot_controller_msgs.PWMServoState;
import ros_robot_controller_msgs.SetPWMServoState;

public final class ControllerNode implements AutoCloseable {
    private static final double[] COV_MOVING = diag(1e-3, 1e-3, 1e6, 1e6, 1e6, 1e3);
    private static final double[] COV_STOP   = stopCov();

    private final RobotConfig cfg;
    private final MecanumChassis mecanum = new MecanumChassis(0.1368, 0.1446, 0.065);
    private final AckermannChassis ackermann = new AckermannChassis(0.145, 0.133, 0.067);
    private final ROS2Node node;
    private final ROS2Publisher<MotorsState> motorPub;
    private final ROS2Publisher<SetPWMServoState> servoPub;
    private final ROS2Publisher<Odometry> odomPub;
    private final double linearFactor;

    private volatile double vx, vy, wz;
    private double x, y, yaw;
    private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
    private long lastNanos = -1;

    public ControllerNode(RobotConfig cfg, double linearFactor) {
        this.cfg = cfg;
        this.linearFactor = linearFactor;
        node = new ROS2Node("odom_publisher", 0);
        motorPub = node.createPublisher(new ROS2Topic<>("ros_robot_controller/set_motor",
                MotorsState.class));
        servoPub = node.createPublisher(new ROS2Topic<>("ros_robot_controller/pwm_servo/set_state",
                SetPWMServoState.class));
        odomPub  = node.createPublisher(new ROS2Topic<>("odom_raw", Odometry.class));

        node.createSubscription(new ROS2Topic<>("controller/cmd_vel", Twist.class),
                reader -> onCmdVel(reader.read(), false));
        node.createSubscription(new ROS2Topic<>("cmd_vel", Twist.class),
                reader -> onCmdVel(reader.read(), true));
        node.createSubscription(new ROS2Topic<>("set_odom", Pose2D.class),
                reader -> { Pose2D p = reader.read(); resetOdom(p.getX(), p.getY(), p.getTheta()); });

        timer.scheduleAtFixedRate(this::publishOdom, 0, 20, TimeUnit.MILLISECONDS);   // 50 Hz
    }

    private void onCmdVel(Twist t, boolean fromApp) {
        double lx = t.getLinear().getX(), ly = t.getLinear().getY(), az = t.getAngular().getZ();
        if (fromApp) { lx = Math.clamp(lx, -0.2, 0.2); ly = Math.clamp(ly, -0.2, 0.2); az = Math.clamp(az, -0.5, 0.5); }

        if (cfg.ackermann()) {
            vx = lx; vy = 0; wz = (az != 0 && lx != 0) ? az : 0;
            AckermannChassis.Command c = ackermann.setVelocity(vx, wz);
            publishMotors(c.rps());
            if (c.servoPulse() != null && az != 0) publishServo(3, c.servoPulse(), 0.02);
        } else {
            vx = lx; vy = ly; wz = az;
            publishMotors(mecanum.setVelocity(vx, vy, wz));
        }
    }

    private void publishMotors(double[] rps) {
        MotorsState msg = new MotorsState();
        for (int i = 0; i < 4; i++) {
            MotorState m = msg.getData().add();
            m.setId(i + 1);
            m.setRps(rps[i]);
        }
        motorPub.publish(msg);
    }

    private void publishServo(int id, int pulse, double duration) {
        SetPWMServoState msg = new SetPWMServoState();
        msg.setDuration(duration);
        PWMServoState s = msg.getState().add();
        s.getId().add(id);
        s.getPosition().add(pulse);
        servoPub.publish(msg);
    }

    private synchronized void publishOdom() {
        long now = System.nanoTime();
        double dt = lastNanos < 0 ? 0 : (now - lastNanos) / 1e9;
        lastNanos = now;
        x += vx * dt * Math.cos(yaw);
        y += vx * dt * Math.sin(yaw);
        yaw += wz * dt;

        Odometry o = new Odometry();
        o.getHeader().getFrameId().append("odom");
        o.getChildFrameId().append("base_footprint");
        Stamp.now(o.getHeader().getStamp());
        o.getPose().getPose().getPosition().setX(linearFactor * x);
        o.getPose().getPose().getPosition().setY(linearFactor * y);
        Quat.fromYaw(yaw, o.getPose().getPose().getOrientation());
        o.getTwist().getTwist().getLinear().setX(vx);
        o.getTwist().getTwist().getLinear().setY(vy);
        o.getTwist().getTwist().getAngular().setZ(wz);
        double[] cov = (vx == 0 && vy == 0 && wz == 0) ? COV_STOP : COV_MOVING;
        System.arraycopy(cov, 0, o.getPose().getCovariance(), 0, 36);       // fixed-size double[36] [verify accessor]
        System.arraycopy(cov, 0, o.getTwist().getCovariance(), 0, 36);
        odomPub.publish(o);
    }

    private synchronized void resetOdom(double nx, double ny, double nyaw) {
        x = nx; y = ny; yaw = nyaw; vx = vy = wz = 0;
        // original also publishes PoseWithCovarianceStamped on "set_pose" for the EKF
    }

    private static double[] diag(double... d) {
        double[] c = new double[36];
        for (int i = 0; i < 6; i++) c[i * 6 + i] = d[i];
        return c;
    }
    private static double[] stopCov() {
        double[] c = diag(1e-9, 1e-3, 1e6, 1e6, 1e6, 1e-9);
        c[1 * 6 + 2] = 1e-9;
        return c;
    }

    @Override public void close() { timer.shutdownNow(); publishMotors(new double[4]); node.close(); }
}
```

Helpers:

```java
import builtin_interfaces.Time;
import geometry_msgs.Quaternion;

public final class Quat {
    public static void fromYaw(double yaw, Quaternion q) {
        q.setX(0); q.setY(0); q.setZ(Math.sin(yaw / 2)); q.setW(Math.cos(yaw / 2));
    }
    public static double yaw(Quaternion q) {
        return Math.atan2(2 * (q.getW() * q.getZ() + q.getX() * q.getY()),
                          1 - 2 * (q.getY() * q.getY() + q.getZ() * q.getZ()));
    }
}

public final class Stamp {
    public static void now(Time t) {
        long n = System.currentTimeMillis() * 1_000_000L;      // wall clock; ROS time = system time without sim
        t.setSec((int) (n / 1_000_000_000L));
        t.setNanosec((int) (n % 1_000_000_000L));
    }
}
```

Run it only **instead of** the original node: stop `odom_publisher` (edit `controller.launch.py` to not include
`odom_publisher.launch.py`, keep the EKF and IMU filter).

**Possible improvement:** the original ignores the wheel encoders. If the board driver publishes measured motor
speeds [verify with `ros2 topic list`], integrate those instead – mecanum forward kinematics:
`vx = (−r1 − r2 + r3 + r4)·πd/4`, `vy = (r1 − r2 + r3 − r4)·πd/4`·(sign check), `wz = (r1 + r2 + r3 + r4)·πd/(4k)`
(derived by inverting §6.1; verify signs on the robot).

### 6.4 Demos and calibration [wiki]

| Python | Java |
|---|---|
| `ros2 topic pub /controller/cmd_vel …` (x −0.6…0.6 m/s, z −2…2 rad/s, +z = left) | `ros.cmdVel(vx, vy, wz)` |
| IMU calibration `imu_calib do_calib` (6 positions → `imu_calib.yaml`) | keep the ROS tool |
| `angular_calib.launch.py` / `linear_calib.launch.py` (test 360° / 1 m, adjust factor in 0.01 steps) | Java test: drive 1 m by `odom`, measure real distance, factor = real / odom; write to `calibrate_params.yaml` with SnakeYAML |

---

## 7. Chapter 4: lidar games (`app/lidar_controller.py`) [src]

Settings: `MAX_SCAN_ANGLE = 240°` (rear part is blocked by the robot), after `enter`: `threshold = 0.6 m`,
`scan_angle = 90°`, `speed = 0.2`. `set_param` = `[threshold (0.3–1.5), scan_angle, speed]`.
PIDs: `yaw = PID(1.6, 0, 0.16)`, `dist = PID(1.7, 0, 0.16)`. Modes via `set_running`: 0 stop, 1 avoidance,
2 following, 3 guarding (not on Ackermann).

Splitting `LaserScan.ranges` (non-G4 lidar, index 0 = front, counter-clockwise):

```java
import sensor_msgs.LaserScan;

public final class LidarGame implements Game {
    private static final double MAX_SCAN_ANGLE = Math.toRadians(240);
    private final MentorPiRos ros;
    private final Pid pidYaw = new Pid(1.6, 0, 0.16), pidDist = new Pid(1.7, 0, 0.16);
    private volatile int mode;
    private double threshold = 0.6, scanAngle = Math.toRadians(90), speed = 0.2;
    private int lastAct;
    private double turnUntil;                                   // seconds, like self.timestamp

    public LidarGame(MentorPiRos ros) { this.ros = ros; ros.onScan(this::onScan); }

    @Override public void enter() { threshold = 0.6; scanAngle = Math.toRadians(90); speed = 0.2; mode = 0; ros.stop(); }
    @Override public void exit()  { mode = 0; ros.stop(); }
    @Override public void setRunning(boolean r) { mode = r ? 1 : 0; }
    @Override public void command(String[] a) {
        if (a[0].equals("set_mode")) { mode = Integer.parseInt(a[1]); if (mode == 0) ros.stop(); }
        if (a[0].equals("set_param")) { threshold = Double.parseDouble(a[1]);
                                        scanAngle = Math.toRadians(Double.parseDouble(a[2]));
                                        speed = Double.parseDouble(a[3]); }
    }

    private synchronized void onScan(LaserScan scan) {
        float[] r = toArray(scan);                              // ranges sequence → float[] [verify accessor]
        double inc = scan.getAngleIncrement();
        int maxIndex = (int) ((MAX_SCAN_ANGLE / 2) / inc);
        int angleIndex = (int) (scanAngle / 2 / inc + 0.5);
        int n = Math.min(angleIndex, maxIndex);
        float[] left = new float[n], right = new float[n];
        for (int i = 0; i < n; i++) { left[i] = r[i]; right[i] = r[r.length - 1 - i]; }

        switch (mode) {
            case 1 -> avoid(left, right);
            case 2 -> follow(left, right, inc, true);
            case 3 -> follow(left, right, inc, false);
            default -> { }
        }
    }

    /** mode 1: time-based turns, 90° for one side, 180° for both */
    private void avoid(float[] left, float[] right) {
        double now = System.nanoTime() / 1e9;
        if (now < turnUntil) return;
        double l = minValid(left), rr = minValid(right);
        if (Double.isNaN(l) || Double.isNaN(rr)) return;
        double w = speed * 6.0;
        if (l <= threshold && rr > threshold) {
            double z = (lastAct != 0 && lastAct != 1) ? w : -w;
            lastAct = 1; ros.cmdVel(speed / 6, 0, z); turnUntil = now + Math.toRadians(90) / w / 2;
        } else if (l <= threshold && rr <= threshold) {
            lastAct = 3; ros.cmdVel(speed / 6, 0, w);  turnUntil = now + Math.toRadians(180) / w / 2;
        } else if (l > threshold && rr <= threshold) {
            double z = (lastAct != 0 && lastAct != 2) ? -w : w;
            lastAct = 2; ros.cmdVel(speed / 6, 0, z); turnUntil = now + Math.toRadians(90) / w / 2;
        } else {
            lastAct = 0; ros.cmdVel(speed, 0, 0);
        }
    }

    /** mode 2 (follow = turn + keep distance) and mode 3 (guard = turn only) */
    private void follow(float[] left, float[] right, double inc, boolean keepDistance) {
        float[] ranges = new float[left.length + right.length];   // right half reversed, then left half
        for (int i = 0; i < right.length; i++) ranges[i] = right[right.length - 1 - i];
        System.arraycopy(left, 0, ranges, right.length, left.length);

        int idx = -1; double dist = Double.MAX_VALUE;
        for (int i = 0; i < ranges.length; i++)
            if (ranges[i] > 0 && Float.isFinite(ranges[i]) && ranges[i] < dist) { dist = ranges[i]; idx = i; }
        if (idx < 0) return;
        double angle = -scanAngle / 2 + inc * idx;                // angle of the nearest point, 0 = front

        double z = 0, vxOut = 0;
        if (dist < threshold && Math.abs(Math.toDegrees(angle)) > 5) {
            pidYaw.update(-angle);
            z = Math.clamp(pidYaw.output(), -speed * 6, speed * 6);
        } else pidYaw.clear();
        if (keepDistance) {
            if (dist < threshold && Math.abs(0.2 - dist) > 0.02) {   // original condition (0.2, not threshold/2)
                pidDist.update(threshold / 2 - dist);
                vxOut = Math.clamp(pidDist.output(), -speed, speed);
            } else pidDist.clear();
        }
        if (Math.abs(z) < 0.008) z = 0;
        if (Math.abs(vxOut) < 0.05) vxOut = 0;
        ros.cmdVel(vxOut, 0, z);
    }

    private static double minValid(float[] a) {
        double m = Double.NaN; int count = 0;
        for (float v : a) if (v > 0 && Float.isFinite(v)) { count++; m = Double.isNaN(m) ? v : Math.min(m, v); }
        return count > 1 ? m : Double.NaN;                       // Python requires len > 1
    }
}
```

Notes [src]: the Ackermann branch and the `G4` lidar branch (different index layout) exist in the source and are
omitted here. `pid_dist.update(threshold/2 − dist)` drives towards 0.3 m (the wiki text says 0.35 m); check the
sign on the robot (positive error = obstacle too close).

---

## 8. Chapter 5: depth camera

### 8.1 Topics and data [wiki]

- Colour: `/ascamera/camera_publisher/rgb0/image` (`rgb8`) – §4.3.
- Depth: `/ascamera/camera_publisher/depth0/image_raw` – 16-bit [verify encoding `16UC1`, unit mm] → `ImageConv.toDepth`.
- Point cloud: `…/depth0/points` (`sensor_msgs/PointCloud2`) [verify name], frame `camera_link`.
- Start the driver: `ros2 launch peripherals depth_camera.launch.py` (keep the vendor driver; there is no Java SDK).

### 8.2 Depth at a pixel (used by `vllm_track`, distance checks)

```java
static double depthMeters(Mat depth16, int u, int v, int window) {
    double sum = 0; int n = 0;
    for (int dy = -window; dy <= window; dy++)
        for (int dx = -window; dx <= window; dx++) {
            int x = Math.clamp(u + dx, 0, depth16.cols() - 1), y = Math.clamp(v + dy, 0, depth16.rows() - 1);
            short[] s = new short[1];
            depth16.get(y, x, s);
            int mm = s[0] & 0xFFFF;
            if (mm > 0) { sum += mm; n++; }
        }
    return n == 0 ? Double.NaN : sum / n / 1000.0;
}
```

### 8.3 PointCloud2 parsing

```java
import sensor_msgs.PointCloud2;
import sensor_msgs.PointField;

/** returns xyz triples; reads field offsets from the message instead of assuming them */
static float[] xyz(PointCloud2 pc) {
    int ox = -1, oy = -1, oz = -1;
    for (int i = 0; i < pc.getFields().size(); i++) {
        PointField f = pc.getFields().get(i);
        String name = f.getName().toString();
        if (name.equals("x")) ox = (int) f.getOffset();
        if (name.equals("y")) oy = (int) f.getOffset();
        if (name.equals("z")) oz = (int) f.getOffset();
    }
    int step = (int) pc.getPointStep(), count = (int) (pc.getWidth() * pc.getHeight());
    ByteBuffer b = pc.getData().getBuffer().duplicate();
    b.order(pc.getIsBigendian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
    float[] out = new float[count * 3];
    for (int i = 0; i < count; i++) {
        int base = i * step;
        out[i * 3] = b.getFloat(base + ox); out[i * 3 + 1] = b.getFloat(base + oy); out[i * 3 + 2] = b.getFloat(base + oz);
    }
    return out;
}
```

(Point clouds are only visualised in the course; no processing algorithm is taught.)

### 8.4 Web real-time monitoring (`http://192.168.149.1:8080/`, RGB only) [wiki]

Equivalent MJPEG server with the JDK's built-in HTTP server:

```java
public final class MjpegServer {
    private final AtomicReference<byte[]> jpeg = new AtomicReference<>();

    public void offer(Mat bgr) {
        MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".jpg", bgr, buf, new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 70));
        jpeg.set(buf.toArray());
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/stream", ex -> {
            ex.getResponseHeaders().add("Content-Type", "multipart/x-mixed-replace; boundary=frame");
            ex.sendResponseHeaders(200, 0);
            try (OutputStream os = ex.getResponseBody()) {
                while (true) {
                    byte[] f = jpeg.get();
                    if (f != null) {
                        os.write(("--frame\r\nContent-Type: image/jpeg\r\nContent-Length: " + f.length + "\r\n\r\n")
                                 .getBytes(StandardCharsets.US_ASCII));
                        os.write(f); os.write("\r\n".getBytes(StandardCharsets.US_ASCII)); os.flush();
                    }
                    Thread.sleep(66);
                }
            } catch (IOException | InterruptedException e) { /* client gone */ }
        });
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
    }
}
```

Port 8080 is used by the original web server – pick another port or stop it.

---

## 9. Chapter 6: mapping

### 9.1 What stays [wiki]

- URDF: package `mentorpi_description` (`mentorpi.xacro`, `mecanum.xacro`, `ack`), frames `base_footprint` →
  `base_link` (z 0.07) → `imu_link` (yaw −1.57), wheels, lidar, camera. Keep `robot_state_publisher`.
- 2D SLAM: `ros2 launch slam slam.launch.py` (slam_toolbox, config `slam/config/slam.yaml`, scan `scan_raw`).
- 3D SLAM: `ros2 launch slam rtabmap_slam.launch.py` (saves on Ctrl+C).
- Viewing: `rviz_slam.launch.py` / `rviz_rtabmap.launch.py` on the VM.

### 9.2 Teleoperation (`teleop_key_control.launch.py`: W/S forward/back, A/D turn)

```java
/** console teleop – reads single keys (run with `stty -icanon -echo` or use a GUI KeyListener) */
static void teleop(MentorPiRos ros) throws IOException {
    double v = 0, w = 0;
    int c;
    while ((c = System.in.read()) != -1) {
        switch (Character.toLowerCase((char) c)) {
            case 'w' -> v = 0.2;  case 's' -> v = -0.2;
            case 'a' -> w = 0.5;  case 'd' -> w = -0.5;
            case ' ' -> { v = 0; w = 0; }
            case 'q' -> { ros.stop(); return; }
            default -> { continue; }
        }
        ros.cmdVel(v, 0, w);
    }
}
```

### 9.3 Saving the map (`map_saver_cli -f map_01` into `~/ros2_ws/src/slam/maps`)

Keep the CLI, or in Java: subscribe `/map` (`nav_msgs/OccupancyGrid`) with **transient-local** durability and
write the standard PGM + YAML pair (same format as `map_saver_cli`: free = 254, occupied = 0, unknown = 205,
thresholds 0.25 / 0.65).

```java
import geometry_msgs.Pose;
import nav_msgs.OccupancyGrid;

static void saveMap(OccupancyGrid g, Path dir, String name) throws IOException {
    int w = (int) g.getInfo().getWidth(), h = (int) g.getInfo().getHeight();
    byte[] occ = new byte[w * h];
    g.getData().getBuffer().rewind(); g.getData().getBuffer().get(occ);    // int8 sequence [verify accessor]
    try (OutputStream os = Files.newOutputStream(dir.resolve(name + ".pgm"))) {
        os.write(("P5\n" + w + " " + h + "\n255\n").getBytes(StandardCharsets.US_ASCII));
        for (int y = h - 1; y >= 0; y--)                                   // PGM rows top-down, grid bottom-up
            for (int x = 0; x < w; x++) {
                int v = occ[y * w + x];
                os.write(v < 0 ? 205 : v >= 65 ? 0 : v <= 25 ? 254 : 205);
            }
    }
    Pose o = g.getInfo().getOrigin();
    Files.writeString(dir.resolve(name + ".yaml"), String.format(Locale.ROOT,
        "image: %s.pgm%nmode: trinary%nresolution: %.3f%norigin: [%.3f, %.3f, %.3f]%nnegate: 0%n"
        + "occupied_thresh: 0.65%nfree_thresh: 0.25%n",
        name, g.getInfo().getResolution(), o.getPosition().getX(), o.getPosition().getY(),
        Quat.yaw(o.getOrientation())));
}

// subscription with transient local, so the latched map is received
ROS2QoSProfile mapQos = new ROS2QoSProfile();
mapQos.reliability(ROS2QoSProfile.Reliability.RELIABLE);
mapQos.durability(ROS2QoSProfile.Durability.TRANSIENT_LOCAL);
mapQos.history(ROS2QoSProfile.History.KEEP_LAST);
mapQos.depth(1);
```

---

## 10. Chapter 7: navigation (Nav2 + AMCL) – goals via topics

Keep the Nav2 stack: `ros2 launch navigation navigation.launch.py map:=map_01` (2D) or
`rtabmap_navigation.launch.py` (3D); params `navigation/config/nav2_params.yaml`, maps in
`slam/maps/<name>.yaml`; launch waits 10 s before starting Nav2 [wiki]. The wiki explains AMCL, costmaps, DWA and
TEB conceptually (`use_teb` launch argument) but shows no parameter values – read `nav2_params.yaml`.

jros2 cannot call the `navigate_to_pose` action, but Nav2 and AMCL also listen on **topics** – the same ones the
RViz buttons use:

| RViz button | Topic | Type |
|---|---|---|
| 2D Pose Estimate | `/initialpose` | `geometry_msgs/PoseWithCovarianceStamped` |
| 2D Goal Pose / "2D Nav Goal" | `/goal_pose` | `geometry_msgs/PoseStamped` (bt_navigator subscription) |
| Waypoints / Nav Through Poses | – | actions only → run the waypoint loop in Java |

```java
import geometry_msgs.PoseStamped;
import geometry_msgs.PoseWithCovarianceStamped;

public final class Navigator {
    private final ROS2Publisher<PoseStamped> goalPub;
    private final ROS2Publisher<PoseWithCovarianceStamped> initPub;
    private volatile double x, y, yaw;                                  // current pose in map frame

    public Navigator(ROS2Node node) {
        goalPub = node.createPublisher(new ROS2Topic<>("/goal_pose", PoseStamped.class));
        initPub = node.createPublisher(new ROS2Topic<>("/initialpose", PoseWithCovarianceStamped.class));
        node.createSubscription(new ROS2Topic<>("/amcl_pose", PoseWithCovarianceStamped.class),
            reader -> {
                PoseWithCovarianceStamped p = reader.read();
                x = p.getPose().getPose().getPosition().getX();
                y = p.getPose().getPose().getPosition().getY();
                yaw = Quat.yaw(p.getPose().getPose().getOrientation());
            });
    }

    public void setInitialPose(double px, double py, double pyaw) {
        PoseWithCovarianceStamped m = new PoseWithCovarianceStamped();
        m.getHeader().getFrameId().append("map");
        Stamp.now(m.getHeader().getStamp());
        m.getPose().getPose().getPosition().setX(px);
        m.getPose().getPose().getPosition().setY(py);
        Quat.fromYaw(pyaw, m.getPose().getPose().getOrientation());
        m.getPose().getCovariance()[0] = 0.25; m.getPose().getCovariance()[7] = 0.25; m.getPose().getCovariance()[35] = 0.07;
        initPub.publish(m);
    }

    public void goTo(double gx, double gy, double gyaw) {
        PoseStamped m = new PoseStamped();
        m.getHeader().getFrameId().append("map");
        Stamp.now(m.getHeader().getStamp());
        m.getPose().getPosition().setX(gx);
        m.getPose().getPosition().setY(gy);
        Quat.fromYaw(gyaw, m.getPose().getOrientation());
        goalPub.publish(m);
    }

    /** blocking: no action result available → wait until close to the goal or timeout */
    public boolean goToAndWait(double gx, double gy, double gyaw, Duration timeout) throws InterruptedException {
        goTo(gx, gy, gyaw);
        long end = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < end) {
            if (Math.hypot(gx - x, gy - y) < 0.15 && Math.abs(angleDiff(gyaw, yaw)) < 0.3) return true;
            Thread.sleep(200);
        }
        return false;
    }

    /** multi-point navigation ("Start Waypoint Following") */
    public void waypoints(List<double[]> points) throws InterruptedException {
        for (double[] p : points) if (!goToAndWait(p[0], p[1], p[2], Duration.ofMinutes(2))) break;
    }

    static double angleDiff(double a, double b) { return Math.atan2(Math.sin(a - b), Math.cos(a - b)); }
}
```

Tolerances should match Nav2's goal checker (`xy_goal_tolerance`, `yaw_goal_tolerance` in `nav2_params.yaml`).
Hiwonder's `navigation_controller` also publishes `navigation_controller/reach_goal` (`std_msgs/Bool`) [wiki ch. 14]
– subscribe to it instead of polling if that node runs.

---

## 11. Chapter 8: ROS + OpenCV

### 11.1 Shared vision utilities

LAB thresholds come from `/home/ubuntu/software/lab_tool/lab_config.yaml`, layout
`lab: { Stereo|Mono: { red: {min: [L,a,b], max: [L,a,b]}, … } }` [src]; edited with the LAB_Tool GUI (keep it).

```java
public final class Vision {
    public record LabRange(Scalar min, Scalar max) {}
    public record Blob(MatOfPoint contour, double area) {}

    @SuppressWarnings("unchecked")
    public static Map<String, LabRange> loadLab(Path yaml, String cameraKey) throws IOException {
        Map<String, Object> root = new Yaml().load(Files.newBufferedReader(yaml));
        Map<String, Object> lab = (Map<String, Object>) root.get("lab");
        Map<String, Map<String, List<Number>>> colors = (Map<String, Map<String, List<Number>>>) lab.get(cameraKey);
        Map<String, LabRange> out = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, List<Number>>> e : colors.entrySet())
            out.put(e.getKey(), new LabRange(scalar(e.getValue().get("min")), scalar(e.getValue().get("max"))));
        return out;
    }
    private static Scalar scalar(List<Number> v) {
        return new Scalar(v.get(0).doubleValue(), v.get(1).doubleValue(), v.get(2).doubleValue());
    }

    /** range around a clicked LAB colour (colour picker in line following / tracking) [src] */
    public static LabRange around(double[] lab, double t) {
        return new LabRange(new Scalar(lab[0] - 100 * t, lab[1] - 50 * t, lab[2] - 50 * t),
                            new Scalar(lab[0] + 100 * t, lab[1] + 50 * t, lab[2] + 50 * t));
    }

    public static Mat mask(Mat lab, LabRange r) {
        Mat m = new Mat();
        Core.inRange(lab, r.min(), r.max(), m);
        Mat k = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(m, m, k);
        Imgproc.dilate(m, m, k);
        return m;
    }

    public static Optional<Blob> largest(Mat mask, double minArea, int approx) {
        List<MatOfPoint> cs = new ArrayList<>();
        Imgproc.findContours(mask, cs, new Mat(), Imgproc.RETR_EXTERNAL, approx);
        Blob best = null;
        for (MatOfPoint c : cs) {
            double a = Math.abs(Imgproc.contourArea(c));
            if (a > minArea && (best == null || a > best.area())) best = new Blob(c, a);
        }
        return Optional.ofNullable(best);
    }
}
```

### 11.2 Colour recognition (`color_detect_demo.py`) [wiki]

Resize → `GaussianBlur(3×3, σ3)` → `BGR2LAB` → per colour red/green/blue: mask, largest contour (area > 50),
winner = largest area; if > 200: `minEnclosingCircle`, scale back to the original size, draw circle and
`"Color: <name>"` at `(10, h−10)`, `FONT_HERSHEY_SIMPLEX`, 0.65, thickness 2. No ROS output.

```java
Optional<String> detect(Mat bgr, Map<String, Vision.LabRange> ranges, Size size) {
    Mat small = new Mat(), lab = new Mat();
    Imgproc.resize(bgr, small, size, 0, 0, Imgproc.INTER_NEAREST);
    Imgproc.GaussianBlur(small, small, new Size(3, 3), 3);
    Imgproc.cvtColor(small, lab, Imgproc.COLOR_BGR2LAB);
    String best = null; Vision.Blob bestBlob = null;
    for (String color : List.of("red", "green", "blue")) {
        Optional<Vision.Blob> b = Vision.largest(Vision.mask(lab, ranges.get(color)), 50, Imgproc.CHAIN_APPROX_NONE);
        if (b.isPresent() && (bestBlob == null || b.get().area() > bestBlob.area())) { best = color; bestBlob = b.get(); }
    }
    if (bestBlob == null || bestBlob.area() <= 200) return Optional.empty();
    Point c = new Point(); float[] r = new float[1];
    Imgproc.minEnclosingCircle(new MatOfPoint2f(bestBlob.contour().toArray()), c, r);
    double sx = bgr.cols() / size.width, sy = bgr.rows() / size.height;      // common.val_map
    Imgproc.circle(bgr, new Point(c.x * sx, c.y * sy), (int) (r[0] * sx), drawColor(best), 2);
    Imgproc.putText(bgr, "Color: " + best, new Point(10, bgr.rows() - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 0.65, drawColor(best), 2);
    return Optional.of(best);
}
```

### 11.3 QR code generate / recognise (`qrcode_creater.py`, `qrcode_detecter.py`) [wiki]

Python `qrcode.QRCode(version=1, error_correction=H, box_size=5, border=4)` and `pyzbar.decode` → ZXing:

```java
static void createQr(String text, Path file) throws WriterException, IOException {
    Map<EncodeHintType, Object> hints = Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H,
                                               EncodeHintType.MARGIN, 4);
    BitMatrix m = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 0, 0, hints);   // 1 px per module
    BufferedImage small = MatrixToImageWriter.toBufferedImage(m);
    int scale = 5;                                                                       // box_size=5
    BufferedImage big = new BufferedImage(small.getWidth() * scale, small.getHeight() * scale, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = big.createGraphics();
    g.drawImage(small, 0, 0, big.getWidth(), big.getHeight(), null);
    g.dispose();
    ImageIO.write(big, "jpg", file.toFile());                                            // e.g. myQRcode.jpg
}

static List<Result> decodeQr(Mat bgr) throws IOException {
    MatOfByte buf = new MatOfByte();
    Imgcodecs.imencode(".png", bgr, buf);
    BufferedImage img = ImageIO.read(new ByteArrayInputStream(buf.toArray()));
    BinaryBitmap bmp = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(img)));
    try {
        Result[] rs = new GenericMultipleBarcodeReader(new MultiFormatReader()).decodeMultiple(bmp);
        for (Result r : rs) {                                    // draw border (0,255,0) 3 px, text above
            ResultPoint[] p = r.getResultPoints();
            for (int i = 0; i < p.length; i++)
                Imgproc.line(bgr, new Point(p[i].getX(), p[i].getY()),
                             new Point(p[(i + 1) % p.length].getX(), p[(i + 1) % p.length].getY()), new Scalar(0, 255, 0), 3);
            Imgproc.putText(bgr, r.getText(), new Point(p[0].getX(), p[0].getY() - 10),
                            Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 2);
        }
        return List.of(rs);
    } catch (NotFoundException e) { return List.of(); }
}
```

(The `encode(…, 0, 0, …)` call yields the minimal matrix; ZXing picks the version automatically – `version=1`
in Python is only a start value with `fit=True`.) `ImageIO.read` from a PNG buffer is simple but slow; for video
rates build the `BufferedImage` directly from the Mat bytes.

### 11.4 Line following (`app/line_following.py`) [src]

Algorithm:

- ROIs `(y0, y1, x0, x1, weight)` as fractions of the image:
  `ascamera`: `(0.90–0.95, 0.7)`, `(0.80–0.85, 0.2)`, `(0.70–0.75, 0.1)`;
  USB camera: `(0.81–0.83, 0.7)`, `(0.69–0.71, 0.2)`, `(0.57–0.59, 0.1)`; full width.
- Per ROI: `RGB2LAB` → blur 3×3 σ3 → inRange → erode/dilate → contours `TC89_L1` → largest (area > 30) →
  `minAreaRect` → centre = midpoint of corners 0 and 2.
- `center = Σ(cx·weight) / Σweight`; with ascamera the width used for the centre is `w + 200` (sic, in source).
- `deflection = −atan((center − w/2) / (h/2))`.
- Control: `linear.x = 0.15`; `PID(1.1, 0, 0)` after `enter` (constructor value `0.005, 0.001, 0` is replaced);
  mecanum: `angular.z = clamp(−pid.output, −1, 1)`; Ackermann: `steer = clamp(−output, ±45°)`,
  `angular.z = v / (0.145 / tan(steer))`.
- Lidar safety: front ±22.5° (`scan_angle = 45°`); if nearest left or right < `stop_threshold` 0.4 m → stop;
  resume after > 5 clear scans.
- Pan-tilt (USB camera only) set to `[1850, 1500]` on enter. Colour from picker (click) or by name
  (`set_large_model_target_color`, used by the LLM demo, §17.4). Lost target > 5 frames with LLM start → stop after 5 s.

```java
import sensor_msgs.LaserScan;

public final class LineFollower implements Game {
    private record Roi(double y0, double y1, double x0, double x1, double weight) {}

    private final MentorPiRos ros;
    private final RobotConfig cfg;
    private final List<Roi> rois;
    private final double weightSum;
    private final Pid pid = new Pid(1.1, 0, 0);
    private volatile Vision.LabRange target;
    private volatile boolean running, obstacle;
    private int clearCount;

    public LineFollower(MentorPiRos ros, RobotConfig cfg) {
        this.ros = ros; this.cfg = cfg;
        rois = cfg.stereo()
            ? List.of(new Roi(0.90, 0.95, 0, 1, 0.7), new Roi(0.80, 0.85, 0, 1, 0.2), new Roi(0.70, 0.75, 0, 1, 0.1))
            : List.of(new Roi(0.81, 0.83, 0, 1, 0.7), new Roi(0.69, 0.71, 0, 1, 0.2), new Roi(0.57, 0.59, 0, 1, 0.1));
        weightSum = rois.stream().mapToDouble(Roi::weight).sum();
        ros.onRgbImage(this::onImage);
        ros.onScan(this::onScan);
    }

    public void setTarget(Vision.LabRange r) { target = r; }
    @Override public void enter() { pid.clear(); running = false; if (!cfg.stereo()) ros.servos(0.5, new int[]{1, 2}, new int[]{1850, 1500}); ros.stop(); }
    @Override public void exit()  { running = false; target = null; ros.stop(); }
    @Override public void setRunning(boolean r) { running = r; if (!r) ros.stop(); }

    private void onImage(Mat bgr) {
        Vision.LabRange t = target;
        if (t == null) return;
        OptionalDouble defl = deflection(bgr, t);
        if (!running) { pid.clear(); return; }
        if (obstacle) { ros.stop(); return; }
        if (defl.isEmpty()) return;
        double v = 0.15;
        pid.update(defl.getAsDouble());
        if (cfg.ackermann()) {
            double steer = Math.clamp(-pid.output(), -Math.toRadians(45), Math.toRadians(45));
            ros.cmdVel(v, 0, steer == 0 ? 0 : v / (0.145 / Math.tan(steer)));
        } else {
            ros.cmdVel(v, 0, Math.clamp(-pid.output(), -1.0, 1.0));
        }
    }

    OptionalDouble deflection(Mat bgr, Vision.LabRange t) {
        int h = bgr.rows(), w = bgr.cols();
        double wEff = cfg.stereo() ? w + 200 : w;                       // as in source
        double centroidSum = 0;
        for (Roi r : rois) {
            Mat blob = bgr.submat((int) (r.y0() * h), (int) (r.y1() * h), (int) (r.x0() * w), (int) (r.x1() * w));
            Mat lab = new Mat();
            Imgproc.cvtColor(blob, lab, Imgproc.COLOR_BGR2LAB);         // Mat is BGR here (see §4.3 note)
            Imgproc.GaussianBlur(lab, lab, new Size(3, 3), 3);
            Optional<Vision.Blob> b = Vision.largest(Vision.mask(lab, t), 30, Imgproc.CHAIN_APPROX_TC89_L1);
            if (b.isEmpty()) continue;
            Point[] box = new Point[4];
            Imgproc.minAreaRect(new MatOfPoint2f(b.get().contour().toArray())).points(box);
            double cx = (box[0].x + box[2].x) / 2;
            centroidSum += cx * r.weight();
        }
        if (centroidSum == 0) return OptionalDouble.empty();
        double center = centroidSum / weightSum;
        return OptionalDouble.of(-Math.atan((center - wEff / 2.0) / (h / 2.0)));
    }

    private void onScan(LaserScan scan) {
        float[] r = LidarUtil.ranges(scan);
        int n = (int) (Math.toRadians(45) / 2 / scan.getAngleIncrement() + 0.5);
        double left = Double.MAX_VALUE, right = Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (r[i] > 0 && Float.isFinite(r[i])) left = Math.min(left, r[i]);
            float rr = r[r.length - 1 - i];
            if (rr > 0 && Float.isFinite(rr)) right = Math.min(right, rr);
        }
        if (left < 0.4 || right < 0.4) { obstacle = true; clearCount = 0; }
        else if (++clearCount > 5) { obstacle = false; clearCount = 0; }
    }
}
```

`LidarUtil.ranges(scan)` copies the `ranges` float sequence into a `float[]` (accessor depends on the generated
class; for IHMC sequences typically `scan.getRanges().get(i)` / `size()`) [verify].

### 11.5 Colour tracking with the pan-tilt (`color_track`, USB camera version) [wiki]

```java
// center = detected circle (x, y) in an image of size (width, height); pulses clamped to 800..1900
yPid.setPoint(width / 2.0);  yPid.update(width - x);  panPulse  -= (int) yPid.output();
zPid.setPoint(height / 2.0); zPid.update(y);          tiltPulse -= (int) zPid.output();
panPulse = Math.clamp(panPulse, 800, 1900); tiltPulse = Math.clamp(tiltPulse, 800, 1900);
ros.servos(0.02, new int[] {1, 2}, new int[] {tiltPulse, panPulse});      // servo 1 tilt, servo 2 pan
```

Gains are in `color_track.zip`. Colour detection: same as §11.2 with `detect_type = 'circle'`.

### 11.6 Chassis tracking (`app/object_tracking.py`) [src]

- Processing size `pro_size`: ascamera `320×180`, USB `320×240`; target stop point `x_stop = 320`,
  `y_stop = 300` (ascamera) / `400` (USB) in full-image pixels; colour range from click (`Vision.around`, t = 0.1).
- `PID yaw(0.006, 0, 0)`, `PID dist(0.002, 0, 0)`; dead bands 20 px.
- `linear.x = clamp(pidDist.update(y − y_stop), −0.35, 0.35)`; `angular.z = clamp(pidYaw.update(x − x_stop), −2, 2)`
  (mecanum) or Ackermann steering `clamp(−output, ±40°)`, `angular.z = −v / (0.145 / tan(steer))`.
- Target lost for 5 frames → stop.

```java
void control(double x, double y) {
    double vx = 0, wz = 0;
    if (Math.abs(y - yStop) > 20) { pidDist.update(y - yStop); vx = Math.clamp(pidDist.output(), -0.35, 0.35); }
    else pidDist.clear();
    if (Math.abs(x - xStop) > 20) {
        pidYaw.update(x - xStop);
        if (cfg.ackermann()) {
            double steer = Math.clamp(-pidYaw.output(), -Math.toRadians(40), Math.toRadians(40));
            wz = steer == 0 ? 0 : -vx / (0.145 / Math.tan(steer));
        } else wz = Math.clamp(pidYaw.output(), -2.0, 2.0);
    } else pidYaw.clear();
    ros.cmdVel(vx, 0, wz);
}
```

---

## 12. Chapter 9: MediaPipe human-robot interaction

MediaPipe has no official Java desktop/ARM API. Options (same as TurboPi document §7):
ONNX-converted MediaPipe models (palm detection + hand landmark, BlazePose) with ONNX Runtime Java, or a Python
sidecar process that publishes landmarks on a ROS topic (`std_msgs/Float32MultiArray`) that Java subscribes to
via jros2. The **logic** below ports 1:1 regardless.

Configurations [wiki]: Hands `static_image_mode=False, max_num_hands=1, min_detection_confidence=0.6,
min_tracking_confidence=0.05`; Pose `min_detection_confidence=0.7, min_tracking_confidence=0.7`.
Input: `flip(image, 1)` then BGR→RGB.

### 12.1 Fingertip trajectory (`hand_gesture.py`) [wiki]

```java
record Lm(double x, double y) {}

static double vecAngle(double ax, double ay, double bx, double by) {
    double c = (ax * bx + ay * by) / (Math.hypot(ax, ay) * Math.hypot(bx, by));
    return Math.toDegrees(Math.acos(Math.clamp(c, -1, 1)));
}
static double angle(List<Lm> h, int a0, int a1, int b0, int b1) {        // angle between (a0→a1) and (b0→b1)
    return Math.abs(vecAngle(h.get(a1).x() - h.get(a0).x(), h.get(a1).y() - h.get(a0).y(),
                             h.get(b1).x() - h.get(b0).x(), h.get(b1).y() - h.get(b0).y()));
}

static double[] fingerAngles(List<Lm> h) {                              // pixel coordinates
    return new double[] {
        angle(h, 3, 4, 0, 2),     // thumb
        angle(h, 0, 6, 7, 8),     // index
        angle(h, 0, 10, 11, 12),  // middle
        angle(h, 0, 14, 15, 16),  // ring
        angle(h, 0, 18, 19, 20),  // pinky
    };
}

static String gesture(double[] a) {
    final double T = 65, TT = 53, S = 49;                               // thr_angle, thr_angle_thumb, thr_angle_s
    boolean b1 = a[1] > T, b2 = a[2] > T, b3 = a[3] > T, b4 = a[4] > T;
    boolean s1 = a[1] < S, s2 = a[2] < S, s3 = a[3] < S, s4 = a[4] < S;
    if (a[0] > TT && b1 && b2 && b3 && b4) return "fist";
    if (a[0] < S && s1 && b2 && b3 && b4) return "hand_heart";
    if (a[0] < S && s1 && b2 && b3 && s4) return "nico-nico-ni";
    if (a[0] > 5 && s1 && b2 && b3 && b4) return "one";
    if (a[0] > TT && s1 && s2 && b3 && b4) return "two";
    if (a[0] > TT && s1 && s2 && s3 && b4) return "three";
    if (a[0] > TT && b1 && s2 && s3 && s4) return "OK";
    if (a[0] > TT && s1 && s2 && s3 && s4) return "four";
    if (a[0] < S && s1 && s2 && s3 && s4) return "five";
    if (a[0] < S && b1 && b2 && b3 && s4) return "six";
    return "none";
}
```

State machine: "one" held > 5 frames → TRACKING, clear points; while tracking (gesture ≠ "two") append the index
fingertip (landmark 8) when it moved ≥ 5 px; "five" → clear and stop. Draw the polyline with `Imgproc.polylines`.

### 12.2 Hand following (`hand_track_node.py`, pan-tilt) [wiki]

Input `/hand_detect/center` (`Point2D`: x, y, width, height); pan: `pid.setPoint(width/2)`,
`pid.update(width − x)`, `pan += output`, clamp ≥ 800 (note the sign differs from §11.5). Tilt analog
(gains in `hand_track.zip`).

### 12.3 Posture control (`body_control.py`) [wiki]

```java
// landmarks in pixels; names per MediaPipe Pose: 11/12 shoulders, 15/16 wrists, 23/24 hips, 25/26 knees, 27/28 ankles
static double sq(Lm a, Lm b) { double dx = a.x() - b.x(), dy = a.y() - b.y(); return dx * dx + dy * dy; }

static int[] bodyStatus(List<Lm> p) {
    double lArm = round1(sq(p.get(23), p.get(11)) / sq(p.get(23), p.get(15)));
    double rArm = round1(sq(p.get(24), p.get(12)) / sq(p.get(24), p.get(16)));
    double lLeg = round1(sq(p.get(23), p.get(27)) / sq(p.get(27), p.get(25)));
    double rLeg = round1(sq(p.get(24), p.get(28)) / sq(p.get(28), p.get(26)));
    return new int[] { lArm < 1 ? 1 : 0, rArm < 1 ? 1 : 0, lLeg > 0 && lLeg < 2 ? 1 : 0, rLeg > 0 && rLeg < 2 ? 1 : 0 };
}
static double round1(double v) { return Math.round(v * 10) / 10.0; }
```

Motion [wiki]: arc moves by direct motor speeds – left: motor 2 = 0.1 rps, motor 4 = −1.0 rps for 11 s; right:
motor 2 = 1.0, motor 4 = −0.1 for 12 s; then stop (publish `MotorsState` on `ros_robot_controller/set_motor`).
Buzzer warning 1900 Hz, 0.2 s. The wiki contradicts itself on which arm means which direction – check
`body_control.zip`.

### 12.4 Fall detection (`fall_down_detect.py`) [wiki]

```java
// h = mean y (pixels) of all 33 pose landmarks; fallen if h > imageHeight - 120
samples.add(h > imageHeight - 120 ? 1 : 0);
if (samples.size() == 3) {
    int sum = samples.stream().mapToInt(Integer::intValue).sum();
    samples.clear();
    if (stopFlag && sum <= 1) { /* still standing */ }
    else if (sum > 1) alarm();       // repeated 1000 Hz beeps + 5× forward/back 0.2 m/s for 0.2 s [wiki]
}
```

---

## 13. Chapter 10: machine learning (YOLOv5)

Workflow [wiki]: collect images (`~/software/collect_picture/main.py`), label with labelImg, classes in
`my_data/classes.names`, convert with `xml2yolo.py`, train
`python3 software/yolov5/train.py --img 640 --batch 8 --epochs 300 --data …/data.yaml --weights yolov5n.pt`,
copy `best.pt` to `~/ros2_ws/src/yolov5_ros2/config`, run `ros2 launch yolov5_ros2 yolov5_ros2.launch.py`.
Course models: `traffic_signs_640s_7_0` (signs), `traffic_signs_640s_7_1` (parking). The robot node uses
TensorRT (`yolov5_trt.py`) [wiki-summary].

**Java:** keep training in Python. Export once: `python3 export.py --weights best.pt --include onnx --imgsz 640`.
YOLOv5 ONNX output is `[1, 25200, 5 + nc]` = `cx, cy, w, h, objectness, class scores…` – **needs NMS**.

```java
public final class Yolov5 implements AutoCloseable {
    public record Det(int cls, String label, float score, Rect2d box) {}
    private final OrtEnvironment env = OrtEnvironment.getEnvironment();
    private final OrtSession session;
    private final List<String> names;
    private final int size = 640;

    public Yolov5(Path onnx, List<String> names) throws OrtException {
        session = env.createSession(onnx.toString(), new OrtSession.SessionOptions());
        this.names = names;
    }

    public List<Det> detect(Mat bgr, float conf, float iou) throws OrtException {
        double scale = Math.min((double) size / bgr.cols(), (double) size / bgr.rows());
        int nw = (int) (bgr.cols() * scale), nh = (int) (bgr.rows() * scale);
        int padX = (size - nw) / 2, padY = (size - nh) / 2;
        Mat resized = new Mat(), input = new Mat(size, size, CvType.CV_8UC3, new Scalar(114, 114, 114));
        Imgproc.resize(bgr, resized, new Size(nw, nh));
        resized.copyTo(input.submat(padY, padY + nh, padX, padX + nw));
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);
        byte[] px = new byte[size * size * 3];
        input.get(0, 0, px);
        FloatBuffer chw = FloatBuffer.allocate(3 * size * size);
        for (int c = 0; c < 3; c++) for (int i = 0; i < size * size; i++) chw.put((px[i * 3 + c] & 0xFF) / 255f);
        chw.rewind();

        List<Rect2d> boxes = new ArrayList<>(); List<Float> scores = new ArrayList<>(); List<Integer> classes = new ArrayList<>();
        try (OnnxTensor t = OnnxTensor.createTensor(env, chw, new long[] {1, 3, size, size});
             OrtSession.Result out = session.run(Map.of(session.getInputNames().iterator().next(), t))) {
            float[][] rows = ((float[][][]) out.get(0).getValue())[0];
            for (float[] r : rows) {
                if (r[4] < conf) continue;
                int best = 5; for (int k = 6; k < r.length; k++) if (r[k] > r[best]) best = k;
                float score = r[4] * r[best];
                if (score < conf) continue;
                double x = (r[0] - r[2] / 2 - padX) / scale, y = (r[1] - r[3] / 2 - padY) / scale;
                boxes.add(new Rect2d(x, y, r[2] / scale, r[3] / scale)); scores.add(score); classes.add(best - 5);
            }
        }
        MatOfInt keep = new MatOfInt();
        MatOfRect2d mb = new MatOfRect2d(); mb.fromList(boxes);
        MatOfFloat ms = new MatOfFloat(); ms.fromList(scores);
        Dnn.NMSBoxes(mb, ms, conf, iou, keep);
        List<Det> res = new ArrayList<>();
        for (int i : keep.toArray()) res.add(new Det(classes.get(i), names.get(classes.get(i)), scores.get(i), boxes.get(i)));
        return res;
    }

    @Override public void close() throws OrtException { session.close(); }
}
```

Typical values: `conf = 0.5`, `iou = 0.45` (the course's confidence is in `self_driving.launch.py`, not readable
on the wiki). The original node publishes detections on `/yolov5_ros2/object_detect` – in Java call `detect()`
directly in the driving loop.

---

## 14. Chapter 11: autonomous driving

Sources: `example/self_driving/lane_detect.py` (verbatim on the wiki), `self_driving.py` (only summary) – download
`self_driving.zip` for the full state logic.

### 14.1 Lane keeping (`lane_detect.py`) [wiki]

Follow the **yellow** border line. ROIs `(y_min, y_max, x_min, x_max, weight)` in **pixels**:
ascamera `(338,360,0,320,0.7)`, `(292,315,0,320,0.2)`, `(248,270,0,320,0.1)`; other cameras
`(450,480,0,320,0.7)`, `(390,480,0,320,0.2)`, `(330,480,0,320,0.1)`. Per ROI: `RGB2LAB`, blur 3×3 σ3, inRange
with `lab['Stereo']['yellow']`, erode/dilate, largest contour (area > 30), `minAreaRect` centre. Output
`angle = toDegrees(−atan((center − w/2) / (h/2)))` and the right-most centre x (`max_center_x`).

Same structure as `LineFollower.deflection` (§11.4) with pixel ROIs; return degrees and the max x as well.

### 14.2 Driving node logic [wiki-summary]

- Inputs: camera, YOLO detections with classes `go, right, park, red, green, crosswalk`.
- Lane PID `PID(0.4, 0, 0.05)` on the lane angle → `angular.z`.
- Crosswalk: distance = largest y of the crosswalk box → slow down / pass.
- Red light → stop; green → go. Right-turn sign counted ≥ 5 times → execute right turn at the crossing.
- Park sign → parking manoeuvre.
- Publishers: `/controller/cmd_vel`, servo state, `~/image_result`.

```java
enum DriveEvent { NONE, RED, GREEN, CROSSWALK, TURN_RIGHT, PARK }

DriveEvent evaluate(List<Yolov5.Det> dets, int imageHeight) {
    for (Yolov5.Det d : dets) {
        switch (d.label()) {
            case "red"       -> { return DriveEvent.RED; }
            case "crosswalk" -> { if (d.box().y + d.box().height > crosswalkY) return DriveEvent.CROSSWALK; }
            case "right"     -> { if (++rightCount >= 5) return DriveEvent.TURN_RIGHT; }
            case "park"      -> { if (d.box().y + d.box().height > parkY) return DriveEvent.PARK; }
            default -> { }
        }
    }
    return DriveEvent.NONE;
}
```

`crosswalkY`, `parkY`, speeds: from `self_driving.py` (the red-boxed values on the wiki are not readable).

### 14.3 Parking [wiki]

- Mecanum: sideways to the right `linear.y = −0.2` for `0.38 / 0.2` s [wiki-summary; the verbatim text says
  0.38/2 s – check the source], then stop.
- Ackermann: `linear.x = 0.15`, steering −0.5061 rad (`angular.z = v·tan(−0.5061)/0.145`) for 3 s, then the other
  direction for 2 s, then reverse `−0.15` for 1.5 s [wiki-summary].

```java
void parkMecanum() throws InterruptedException {
    ros.cmdVel(0, -0.2, 0); Thread.sleep((long) (0.38 / 0.2 * 1000)); ros.stop();
}
void parkAckermann() throws InterruptedException {
    double v = 0.15, steer = -0.5061;
    ros.cmdVel(v, 0, v * Math.tan(steer) / 0.145);   Thread.sleep(3000);
    ros.cmdVel(v, 0, -v * Math.tan(steer) / 0.145);  Thread.sleep(2000);
    ros.cmdVel(-v, 0, 0);                            Thread.sleep(1500);
    ros.stop();
}
```

---

## 15. Chapter 12: master-slave and group control

Setup [wiki]: master robot = hotspot, slaves in LAN mode on it; env `/home/ubuntu/ros2_ws/.typerc` with
`MASTER`/`HOST` → namespaces `robot_1` (master), `robot_2`, …; clocks synchronised with `date -s`.
Launch per robot `ros2 launch multi multi_controller.launch.py`; on the master `ros2 run joy joy_node` and
`joystick_control_multi.py`. Left stick: forward/back and (mecanum) turn; right stick: turn (Ackermann);
START: stop + both buzzers.

Java: one node on the master subscribes `/joy` and publishes the same Twist to every robot's namespace.

```java
import geometry_msgs.Twist;
import sensor_msgs.Joy;

public final class GroupControl {
    private final List<ROS2Publisher<Twist>> robots = new ArrayList<>();
    private final Twist twist = new Twist();

    public GroupControl(ROS2Node node, RobotConfig cfg, List<String> namespaces) {   // e.g. List.of("/robot_1", "/robot_2")
        for (String ns : namespaces)
            robots.add(node.createPublisher(new ROS2Topic<>(ns + "/controller/cmd_vel", Twist.class)));
        node.createSubscription(new ROS2Topic<>("/joy", Joy.class), reader -> {
            Joy j = reader.read();
            float ly = j.getAxes().get(1), lx = j.getAxes().get(0), rx = j.getAxes().get(3);   // axis indices [verify with ros2 topic echo /joy]
            double v = 0.4 * ly;                                    // light push = slow
            double w = cfg.ackermann() ? 1.0 * rx : 1.5 * lx;
            synchronized (twist) {
                twist.getLinear().setX(v); twist.getAngular().setZ(w);
                for (ROS2Publisher<Twist> p : robots) p.publish(twist);
            }
        });
    }
}
```

All robots must use the same `ROS_DOMAIN_ID` (0) and be on the same subnet. The wiki gives no formation logic;
followers receive identical commands. Speeds/axis mapping: check `multi.zip`.

---

## 16. (Chapter 13 – network: see §5.3)

---

## 17. Chapter 14: large AI models and voice

### 17.1 WonderEcho Pro (wake word)

USB device with audio + CH340 serial (`/dev/ttyUSB0` or `/dev/ttyCH341USB1`), 115200 baud [wiki]. The MentorPi
page does **not** state the wake frame bytes; the TurboPi wiki documents **`AA 55 03 00 FB`** for the same
module [verify with a serial monitor]. Java implementation: TurboPi document §10.1 (`WonderEchoPro` class with
jSerialComm). Fan off while listening: `pinctrl FAN_PWM op dh`, restore `a0`.

Audio: 16 kHz, S16_LE (`arecord -D hw:0,0 -f S16_LE -r 16000 -c 2`) → `javax.sound.sampled` (TurboPi §10.2).

### 17.2 Models and configuration [wiki]

- LLM `gpt-4o-mini`; ASR `whisper-1` (or `gpt-4o-transcribe`), VAD `threshold=0.2, prefix_padding_ms=300,
  silence_duration_ms=800`; TTS `gpt-4o-mini-tts` / `tts-1` / `tts-1-hd`, voices nova … coral.
- Vision LLM via OpenRouter (`vllm_model`, `vllm_api_key`, `vllm_base_url`); Chinese mode StepFun.
- Config `~/ros2_ws/src/large_models/large_models/config.py` (`llm_model`, `api_key`, `base_url`, `vllm_*`,
  `stepfun_*`, audio paths). Java: environment variables or a `config.properties` outside git.
- openai-java calls for ASR / chat / TTS: TurboPi document §10.3. For OpenRouter/StepFun create a second client
  with `.baseUrl(vllmBaseUrl).apiKey(vllmApiKey)`.

Vision request (image + text) with openai-java:

```java
String describe(OpenAIClient vllm, String model, Mat bgr, String question) {
    MatOfByte buf = new MatOfByte();
    Imgcodecs.imencode(".jpg", bgr, buf);
    String dataUrl = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(buf.toArray());
    ChatCompletionContentPart text = ChatCompletionContentPart.ofText(
            ChatCompletionContentPartText.builder().text(question).build());
    ChatCompletionContentPart image = ChatCompletionContentPart.ofImageUrl(
            ChatCompletionContentPartImage.builder()
                .imageUrl(ChatCompletionContentPartImage.ImageUrl.builder().url(dataUrl).build()).build());
    ChatCompletionCreateParams p = ChatCompletionCreateParams.builder()
            .model(model)
            .addUserMessageOfArrayOfContentParts(List.of(text, image))
            .build();
    return vllm.chat().completions().create(p).choices().get(0).message().content().orElse("");
}
```

(Builder method names follow openai-java's generated API – check against the version you use.)

### 17.3 Architecture of the original vs Java

The Python version splits voice into ROS nodes (`vocal_detect` → wake, `agent_process` → LLM, `tts_node` → TTS)
connected by topics **and services** (`agent_process/set_prompt`, `set_model`, `set_vllm_content`,
`vocal_detect/enable_wakeup`, `navigation_controller/set_pose`, …). In Java all of this is **one process**:

```java
public final class VoiceAgent {
    private final WonderEchoPro echo; private final Speech speech; private final OpenAIClient llm;
    private final ActionDispatcher actions;
    private final String prompt;
    private final AtomicBoolean interrupt = new AtomicBoolean();

    public void run() throws Exception {
        speech.play(Path.of("resources/audio/en/start_audio.wav"));
        while (!Thread.currentThread().isInterrupted()) {
            if (!echo.wakeup()) { Thread.sleep(20); continue; }
            interrupt.set(true);                               // wake while acting = stop (all apps do this)
            actions.stopAll();
            interrupt.set(false);
            speech.play(Path.of("resources/audio/en/wakeup.wav"));
            String text = speech.listen();                     // record with VAD + whisper-1
            if (text.isBlank()) { speech.play(Path.of("resources/audio/en/no_voice.wav")); continue; }
            String json = speech.chat(llm, "gpt-4o-mini", prompt, text);
            actions.handle(json, interrupt);
        }
    }
}
```

Parse LLM output leniently (prompts specify single-quoted pseudo-JSON):

```java
static JsonNode parseLlm(String raw) throws JsonProcessingException {
    int s = raw.indexOf('{'), e = raw.lastIndexOf('}');
    if (s < 0 || e < s) throw new JsonParseException(null, "no JSON object in LLM output");
    ObjectMapper m = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
            .enable(JsonReadFeature.ALLOW_TRAILING_COMMA)
            .build();
    return m.readTree(raw.substring(s, e + 1));
}
```

### 17.4 The voice apps [wiki]

| App | Prompt/actions | Java |
|---|---|---|
| `llm_control_move` | movement commands (e.g. "Move forward for 2 seconds, then rotate clockwise") → `action` list (vocabulary in source) | dispatcher → `ros.cmdVel` + sleep + stop |
| `llm_visual_patrol` | `line_following('black')`, `{'action':[…], 'response':'…'}`, response 10–30 words | `lineFollower.setTarget(lab.get(color)); setRunning(true)`; stop after 5 s lost |
| `llm_color_track` | `object_tracking('red'|'green'|'blue')` | `objectTracker.setTarget(lab.get(color))` (`lab[Stereo|Mono][color]`) |
| `vllm_with_camera` | describe the scene | `describe(vllm, model, latestFrame, question)` → TTS |
| `vllm_track` | VLM returns `{"object": "...", "xyxy": [x1,y1,x2,y2]}` (normalised → ×640, ×480) | init a tracker (OpenCV `TrackerKCF`/`TrackerCSRT` or colour) + depth (§8.2) → PID (`kp1…kd2`) → `cmdVel`; Ackermann steering clamp `322/2000·180°`, `R = 0.145/tan(steer)` |
| `vllm_navigation` | `move('kitchen')`, `vision('…')`, `play_audio()`; response 5–20 chars | `Navigator.goToAndWait` (§10) with the position table below, `describe(...)`, TTS |

Positions from the wiki (`x, y, roll, pitch, yaw` – the example yaw values are in **degrees** for some entries and
look like quaternion parts for others; re-teach them in RViz on your own map):

```java
Map<String, double[]> places = Map.of(
    "kitchen",        new double[] {3.56647, -2.01839, 0.0},
    "front desk",     new double[] {1.93939,  1.06236, 0.0},
    "bedroom",        new double[] {3.14153, -0.321892, 0.0},
    "zoo",            new double[] {1.3,      0.37,     0.0},
    "space base",     new double[] {1.58,    -0.74,     Math.toRadians(-48)},
    "football field", new double[] {0.32,    -0.65,     Math.toRadians(-90)},
    "origin",         new double[] {0.0,      0.0,      0.0},
    "home",           new double[] {0.1,      0.4,      0.0});
```

Dispatcher for `vllm_navigation`:

```java
private static final Pattern CALL = Pattern.compile("(\\w+)\\(\\s*['\"]?([^'\"]*)['\"]?\\s*\\)");

void handle(String raw, AtomicBoolean interrupt) throws Exception {
    JsonNode n = parseLlm(raw);
    String response = n.path("response").asText();
    speech.speak(response);
    for (JsonNode a : n.path("action")) {
        if (interrupt.get()) break;
        Matcher m = CALL.matcher(a.asText());
        if (!m.matches()) continue;
        switch (m.group(1)) {
            case "move" -> {
                double[] p = places.get(m.group(2));
                if (p != null) navigator.goToAndWait(p[0], p[1], p[2], Duration.ofMinutes(3));
            }
            case "vision"     -> speech.speak(describe(vllm, vllmModel, camera.latest(), VLLM_PROMPT + m.group(2)));
            case "play_audio" -> speech.speak(response);
            default -> { }                                     // unknown actions are ignored, as in the original
        }
    }
}
```

`VLLM_PROMPT` (wiki): butler role, "Do not ask questions in return", 20–40 words.

---

## 18. Open points to resolve on the robot

1. Download the remaining source zips (§0) – PID gains, thresholds, prompts and action vocabularies are there.
2. `ros2 topic list -t` with the full stack running: lidar, camera, IMU, motor feedback, joystick topics.
3. Copy `ros_robot_controller_msgs` and `interfaces`, generate classes, check accessor names (`getState().add()`,
   `getData().add()`, covariance arrays, `ranges` sequence).
4. Container: `RMW_IMPLEMENTATION`, network mode, where to run the jar.
5. Ackermann: steering sign and limit (source 45° vs wiki 29°/36°), servo 3 centre after deviation adjustment.
6. Whether Nav2's `bt_navigator` in this setup accepts `/goal_pose` (Humble default: yes) and the goal tolerances.
7. WonderEcho Pro wake frame on this firmware.
8. Lidar following sign (`pid_dist`) and target distance (0.3 m code vs 0.35 m wiki).
9. Colour order: Python uses `rgb8` + `RGB2LAB`; keep Java consistent (§4.3).
10. jros2: version 1.5.1 availability, `nav_msgs`/`sensor_msgs.Joy`/`PointCloud2` bundled, services/actions in
    newer releases (would replace §4.4 and §10 workarounds).

## 19. Suggested implementation order

1. Maven + message generation; `MentorPiRos`; drive with `cmdVel` (`ros2 topic echo /controller/cmd_vel`).
2. `ControllerNode` (§6) replacing `odom_publisher`; compare `odom_raw` with the original.
3. Lidar games (§7) – only needs `/scan_raw` and `cmd_vel`.
4. OpenCV: colour recognition, QR, line following, chassis tracking (§11).
5. Mapping with slam_toolbox + Java teleop and map saver; navigation goals and waypoints (§9, §10).
6. YOLOv5 ONNX + autonomous driving (§13, §14).
7. Voice agent + LLM apps (§17); vision LLM; voice navigation.
8. MediaPipe features (§12) via sidecar, later ONNX; group control (§15).
