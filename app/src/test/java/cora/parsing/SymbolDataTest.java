/**************************************************************************************************
 Copyright 2019, 2022, 2023 Cynthia Kop

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the License for the specific language governing permissions and limitations under the License.
 *************************************************************************************************/

package cora.parsing;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import cora.types.TypeFactory;
import cora.terms.*;
import cora.rewriting.Alphabet;
import cora.rewriting.*;

/** This class tests the helper class SymbolData that stores information on function symbols. */
public class SymbolDataTest {
  @Test
  public void testBasics() {
    SymbolData data = new SymbolData();
    FunctionSymbol bing = TermFactory.createConstant("bing", TypeFactory.createSort("a"));
    Variable bong = TermFactory.createVar("bong", TypeFactory.createSort("b"));
    MetaVariable bang = TermFactory.createMetaVar("bang",
      TypeFactory.createArrow(TypeFactory.createSort("a"), TypeFactory.createSort("b")), 1);
    data.addFunctionSymbol(bing);
    assertTrue(data.lookupFunctionSymbol("bing").equals(bing));
    assertTrue(data.lookupFunctionSymbol("a") == null);
    data.addVariable(bong);
    assertTrue(data.lookupVariable("bong").equals(bong));
    assertTrue(data.lookupVariable("bing") == null);
    data.addMetaVariable(bang);
    assertTrue(data.lookupMetaVariable("bang").equals(bang));
    assertTrue(data.lookupMetaVariable("bong") == null);
    assertTrue(data.lookupMetaVariable("bing") == null);
    assertTrue(data.lookupVariable("bang") == null);
    data.clearEnvironment();
    assertTrue(data.lookupVariable("bong") == null);
    assertTrue(data.lookupMetaVariable("bang") == null);
  }

  @Test
  public void testCounts() {
    SymbolData data = new SymbolData();
    FunctionSymbol bing = TermFactory.createConstant("bing", TypeFactory.createSort("a"));
    Variable bongv = TermFactory.createVar("bong", TypeFactory.createSort("b"));
    FunctionSymbol bongf = TermFactory.createConstant("bong", TypeFactory.createSort("a"));
    MetaVariable bang = TermFactory.createMetaVar("bang",
      TypeFactory.createArrow(TypeFactory.createSort("a"), TypeFactory.createSort("b")), 1);
    data.addFunctionSymbol(bing);
    data.addFunctionSymbol(bongf);
    data.addFunctionSymbol(bing);
    assertTrue(data.queryNumberFunctionSymbols() == 2);
    assertTrue(data.queryNumberVariables() == 0);
    assertTrue(data.queryNumberMetaVariables() == 0);
    data.addVariable(bongv);
    assertTrue(data.queryNumberFunctionSymbols() == 2);
    assertTrue(data.queryNumberVariables() == 1);
    assertTrue(data.queryNumberMetaVariables() == 0);
    data.addMetaVariable(bang);
    assertTrue(data.queryNumberFunctionSymbols() == 2);
    assertTrue(data.queryNumberVariables() == 1);
    assertTrue(data.queryNumberMetaVariables() == 1);
  }

  @Test
  public void testEqualNamedSymbols() {
    SymbolData data = new SymbolData();
    FunctionSymbol bing1 = TermFactory.createConstant("bing", TypeFactory.createSort("a"));
    Variable bing2 = TermFactory.createVar("bing", TypeFactory.createSort("b"));
    MetaVariable bing3 = TermFactory.createMetaVar("bing",
      TypeFactory.createArrow(TypeFactory.createSort("a"), TypeFactory.createSort("b")), 1);
    data.addFunctionSymbol(bing1);
    data.addVariable(bing2);
    data.addMetaVariable(bing3);
    assertTrue(data.lookupFunctionSymbol("bing").equals(bing1));
    assertTrue(data.lookupVariable("bing").equals(bing2));
    assertTrue(data.lookupMetaVariable("bing").equals(bing3));
  }

  @Test
  public void testLookupNonExisting() {
    SymbolData data = new SymbolData();
    data.addFunctionSymbol(TermFactory.createConstant("bing", TypeFactory.createSort("a")));
    assertTrue(data.lookupFunctionSymbol("bong") == null);
    assertTrue(data.lookupVariable("bing") == null);
    assertTrue(data.lookupMetaVariable("bing") == null);
    assertTrue(data.lookupFunctionSymbol("12") == null);
    assertTrue(data.lookupFunctionSymbol("false") == null);
  }

  @Test
  public void testFunctionSymbolLegalOverride() {
    SymbolData data = new SymbolData();
    data.addFunctionSymbol(TermFactory.createConstant("bing", TypeFactory.createSort("a")));
    data.addFunctionSymbol(TermFactory.createConstant("bing", TypeFactory.createSort("a")));
    assertTrue(data.lookupFunctionSymbol("bing").queryType().equals(TypeFactory.createSort("a")));
  }

  @Test
  public void testVariableLegalOverride() {
    SymbolData data = new SymbolData();
    Variable x = TermFactory.createVar("bing", TypeFactory.createSort("a"));
    data.addVariable(x);
    data.addVariable(x);
    assertTrue(data.lookupVariable("bing").queryType().equals(TypeFactory.createSort("a")));
  }

  @Test
  public void testMetaVariableLegalOverride() {
    SymbolData data = new SymbolData();
    MetaVariable x = TermFactory.createMetaVar("bing",
      TypeFactory.createArrow(TypeFactory.createSort("o"), TypeFactory.createSort("o")), 1);
    data.addMetaVariable(x);
    data.addMetaVariable(x);
    assertTrue(data.lookupMetaVariable("bing") == x);
  }

  @Test
  public void testVariableRemoval() {
    SymbolData data = new SymbolData();
    Variable x = TermFactory.createVar("bing", TypeFactory.createSort("a"));
    data.addVariable(x);
    data.removeVariable("bing");
    assertTrue(data.lookupVariable("bing") == null);
    data.addVariable(TermFactory.createVar("bing", TypeFactory.createSort("q"))); // no error
  }

  @Test(expected = java.lang.Error.class)
  public void testFunctionSymbolIllegalOverride() {
    SymbolData data = new SymbolData();
    data.addFunctionSymbol(TermFactory.createConstant("bing", TypeFactory.createSort("a")));
    data.addFunctionSymbol(TermFactory.createConstant("bing", TypeFactory.createSort("b")));
  }

  @Test(expected = java.lang.Error.class)
  public void testVariableIllegalOverride() {
    SymbolData data = new SymbolData();
    //  variables with the same name and type are not equal if they are different objects
    data.addVariable(TermFactory.createVar("bing", TypeFactory.createSort("a")));
    data.addVariable(TermFactory.createVar("bing", TypeFactory.createSort("a")));
  }

  @Test
  public void testMetaVariableOfArityZero() {
    SymbolData data = new SymbolData();
    MetaVariable x = TermFactory.createMetaVar("bing", TypeFactory.unitSort, 0);
    data.addMetaVariable(x);
    assertTrue(data.lookupMetaVariable("bing") == x);
    assertTrue(data.lookupMetaVariable("bing") == x);
  }

  @Test(expected = java.lang.Error.class)
  public void testMetaVariableIllegalOverride() {
    SymbolData data = new SymbolData();
    // meta-variables with the same name, type and arity are not equal if they are different objects
    data.addMetaVariable(TermFactory.createMetaVar("bing", TypeFactory.createSort("a"), 0));
    data.addMetaVariable(TermFactory.createMetaVar("bing", TypeFactory.createSort("a"), 0));
  }

  @Test
  public void testGenerateAlphabet() {
    SymbolData data = new SymbolData();
    FunctionSymbol bing = TermFactory.createConstant("bing", TypeFactory.createSort("a"));
    FunctionSymbol bong = TermFactory.createConstant("bong", TypeFactory.createSort("a"));
    Variable bang = TermFactory.createVar("bang", TypeFactory.createSort("b"));
    data.addFunctionSymbol(bing);
    data.addFunctionSymbol(bong);
    data.addFunctionSymbol(bing);
    data.addVariable(bang);
    Alphabet alf = data.queryCurrentAlphabet();
    assertTrue(alf.lookup("bing").equals(bing));
    assertTrue(alf.lookup("bong").equals(bong));
    assertTrue(alf.lookup("bang") == null);
    FunctionSymbol bangf = TermFactory.createConstant("bang", TypeFactory.createSort("a"));
    data.addFunctionSymbol(bangf);
    assertTrue(alf.lookup("bang") == null);
  }

  @Test
  public void testInitialiseWithTRS() {
    ArrayList<FunctionSymbol> symbols = new ArrayList<FunctionSymbol>();
    FunctionSymbol bing = TermFactory.createConstant("bing", TypeFactory.createSort("a"));
    FunctionSymbol bong = TermFactory.createConstant("bong", TypeFactory.createSort("b"));
    symbols.add(bing);
    Alphabet alf = new Alphabet(symbols);
    TRS trs = TRSFactory.createMSTRS(alf, new ArrayList<Rule>());
    SymbolData data = new SymbolData(trs);

    assertTrue(data.lookupFunctionSymbol("bing").equals(bing));
    assertTrue(data.lookupVariable("bing") == null);

    data.addFunctionSymbol(bong);

    assertTrue(data.lookupFunctionSymbol("bing").equals(bing));
    assertTrue(data.lookupFunctionSymbol("bong").equals(bong));
  }

  @Test(expected =  java.lang.Error.class)
  public void testVariableIllegalOverrideInAlphabet() {
    ArrayList<FunctionSymbol> symbols = new ArrayList<FunctionSymbol>();
    FunctionSymbol bing = TermFactory.createConstant("bing", TypeFactory.createSort("a"));
    symbols.add(bing);
    Alphabet alf = new Alphabet(symbols);
    TRS trs = TRSFactory.createMSTRS(alf, new ArrayList<Rule>());
    SymbolData data = new SymbolData(trs);

    FunctionSymbol bong = TermFactory.createConstant("bing", TypeFactory.createSort("b"));
    data.addFunctionSymbol(bong);
  }
}

