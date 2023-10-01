/**************************************************************************************************
 Copyright 2023 Cynthia Kop

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the License for the specific language governing permissions and limitations under the License.
 *************************************************************************************************/

package cora.smt;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class AdditionTest {
  @Test
  public void testStaggeredCreation() {
    Addition plus = new Addition(new IValue(3), new Addition(new IVar(12), new IValue(-2)));
    assertTrue(plus.numChildren() == 3);
    assertTrue(plus.queryChild(1).equals(new IValue(3)));
    assertTrue(plus.queryChild(2).equals(new IVar(12)));
    assertTrue(plus.queryChild(3).equals(new IValue(-2)));
  }

  @Test
  public void testEquality() {
    IntegerExpression plus = new Addition(new IValue(1), new IValue(2));
    assertTrue(plus.equals(new Addition(new IValue(1), new IValue(2))));
    assertFalse(plus.equals(new Multiplication(new IValue(1), new IValue(2))));
    assertFalse(plus.equals(new IValue(3)));
  }

  @Test
  public void testToString() {
    ArrayList<IntegerExpression> args = new ArrayList<IntegerExpression>();
    args.add(new IValue(-3));
    args.add(new IValue(7));
    args.add(new IVar(0));
    IntegerExpression plus = new Addition(args);
    assertTrue(plus.toString().equals("(+ (- 3) 7 i0)"));
  }

  @Test
  public void testLegalEvaluate() {
    IntegerExpression plus =
      new Addition(new IValue(3), new Addition(new IValue(12), new IValue(-2)));
    assertTrue(plus.evaluate() == 13);
  }

  @Test(expected = cora.exceptions.IndexingError.class)
  public void testQueryZeroChild() {
    Addition plus = new Addition(new IValue(0), new IVar(2));
    plus.queryChild(0);
  }

  @Test(expected = cora.exceptions.IndexingError.class)
  public void testQueryTooLargeChild() {
    Addition plus = new Addition(new IValue(0), new IVar(2));
    plus.queryChild(3);
  }
}
