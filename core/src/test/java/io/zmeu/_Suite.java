package io.zmeu;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
        "io.zmeu.Frontend.Parse",
        "io.zmeu.TypeChecker",
        "io.zmeu.Runtime",
        "io.zmeu.Frontend.Token",
        "io.zmeu.Frontend.Lexical",
        "io.zmeu.Engine",
        "io.zmeu.Dummy"
})
public class _Suite {
}
