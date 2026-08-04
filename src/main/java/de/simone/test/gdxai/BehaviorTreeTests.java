/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.simone.test.gdxai;

import de.simone.test.gdxai.btree.BehaviorTreeTestBase;
import de.simone.test.gdxai.btree.tests.IncludeSubtreeTest;
import de.simone.test.gdxai.btree.tests.ParallelVsSequenceTest;
import de.simone.test.gdxai.btree.tests.ParseAndCloneTreeTest;
import de.simone.test.gdxai.btree.tests.ParseTreeTest;
import de.simone.test.gdxai.btree.tests.ProgrammaticallyCreatedTreeTest;
import de.simone.test.gdxai.btree.tests.ResumeVsJoinTest;
import de.simone.test.gdxai.btree.tests.SemaphoreGuardTest;

/**
 * Test class for behavior trees.
 * 
 * @author davebaol
 */
public class BehaviorTreeTests {
    private BehaviorTreeTestBase currentTest;

    private BehaviorTreeTestBase[] tests = {
            new ParseTreeTest(),
            new ParseAndCloneTreeTest(false),
            new ParseAndCloneTreeTest(true),
            new IncludeSubtreeTest(false),
            new IncludeSubtreeTest(true),
            new ParallelVsSequenceTest(BehaviorTreeTests.this),
            new ResumeVsJoinTest(BehaviorTreeTests.this),
            new ProgrammaticallyCreatedTreeTest(false),
            new ProgrammaticallyCreatedTreeTest(true),
            new SemaphoreGuardTest()
    };

    public static void main(String[] argv) {
        BehaviorTreeTests behaviorTreeTests = new BehaviorTreeTests();
        behaviorTreeTests.changeTest(0);
    }

    private void changeTest(int testIndex) {
        // Dispose the previous test (if any)
        if (currentTest != null)
            currentTest.dispose();

        // Add the new test
        currentTest = tests[testIndex];
        System.out.println("***********************************************");
        System.out.println("Starting test " + currentTest.getName());
        System.out.println("***********************************************");
        String description = currentTest.getDescription();
        if (description != null) {
            System.out.println(description);
            System.out.println("***********************************************");
        } else {
            System.out.println("Run the tree, look at the log and see what happens");
        }
    }
}
