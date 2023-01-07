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

package cora.terms;

import org.junit.Test;
import static org.junit.Assert.*;

public class HeadPositionTest {
  @Test
  public void testEmptyHeadPosition() {
    HeadPosition hpos = new HeadPosition(new EmptyPosition());
    assertTrue(hpos.queryPosition().isEmpty());
    assertTrue(hpos.queryChopCount() == 0);
    assertTrue(hpos.isEnd());
    assertFalse(hpos.isArgument());
    assertTrue(hpos.equals(new HeadPosition(new EmptyPosition())));
    assertFalse(hpos.equals(new HeadPosition(new EmptyPosition(), 1)));
    assertFalse(hpos.equals(new HeadPosition(new ConsPosition(3,new EmptyPosition()))));
    assertTrue(hpos.toString().equals("ε"));
  }

  @Test
  public void testImmediateHeadPosition() {
    HeadPosition hpos = new HeadPosition(new EmptyPosition(), 3);
    assertTrue(hpos.queryPosition().isEmpty());
    assertTrue(hpos.queryChopCount() == 3);
    assertTrue(hpos.isEnd());
    assertFalse(hpos.isArgument());
    assertFalse(hpos.equals(new HeadPosition(new EmptyPosition())));
    assertTrue(hpos.equals(new HeadPosition(new EmptyPosition(), 3)));
    assertTrue(hpos.toString().equals("☆3"));
  }

  @Test(expected = cora.exceptions.InappropriatePatternDataError.class)
  public void testNoArgument() {
    HeadPosition hpos = new HeadPosition(new EmptyPosition(), 3);
    hpos.queryArgumentPosition();
  }

  @Test(expected = cora.exceptions.InappropriatePatternDataError.class)
  public void testHeadPositionNoTail() {
    HeadPosition hpos = new HeadPosition(new EmptyPosition(), 1);
    hpos.queryTail();
  }

  @Test
  public void testConsHeadPosition() {
    Position pos = new ConsPosition(1, new ConsPosition(2, new EmptyPosition()));
    HeadPosition hpos = new HeadPosition(pos, 2);
    HeadPosition hpos2 = new HeadPosition(pos);
    assertTrue(hpos.toString().equals("1.2.☆2"));
    assertTrue(hpos2.toString().equals("1.2.ε"));
    assertFalse(hpos.equals(hpos2));
    assertTrue(hpos.queryArgumentPosition() == 1);
    assertTrue(hpos.queryTail().toString().equals("2.☆2"));
    assertFalse(hpos.isEnd());
    assertTrue(pos.isArgument());
  }
}

