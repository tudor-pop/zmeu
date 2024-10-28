package io.zmeu;

import org.junit.platform.suite.api.SelectPackages;

@org.junit.platform.suite.api.Suite
@SelectPackages({"io.zmeu.Frontend.Parse","io.zmeu.TypeChecker","io.zmeu.Runtime","io.zmeu.Frontend.Token","io.zmeu.Frontend.Lexical","io.zmeu.Engine"})
public class _Suite {
}
