package io.github.lavy.plugin.oss;

import java.util.Set;

import org.apache.maven.plugin.MojoFailureException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link OssMojo}.
 */
public class OssMojoTest {

    @Test
    public void shouldParseCommaSeparatedSuffixes() throws Exception {
        Set<String> suffixes = OssMojo.parseFileSuffixes("jar, pom,.zip");

        Assert.assertTrue(suffixes.contains(".jar"));
        Assert.assertTrue(suffixes.contains(".pom"));
        Assert.assertTrue(suffixes.contains(".zip"));
        Assert.assertEquals(3, suffixes.size());
    }

    @Test(expected = MojoFailureException.class)
    public void shouldRejectBlankSuffixes() throws Exception {
        OssMojo.parseFileSuffixes(" , ");
    }
}
