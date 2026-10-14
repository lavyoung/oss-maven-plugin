package io.github.lavy.plugin.oss.strategy;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link ObjectNameBuilder}.
 */
public class ObjectNameBuilderTest {

    @Test
    public void shouldUseFileNameWhenPathIsBlank() {
        Assert.assertEquals("demo.jar", ObjectNameBuilder.build("", "demo.jar"));
        Assert.assertEquals("demo.jar", ObjectNameBuilder.build(null, "demo.jar"));
    }

    @Test
    public void shouldTrimLeadingAndTrailingSlashes() {
        Assert.assertEquals("release/demo.jar", ObjectNameBuilder.build("/release/", "demo.jar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectBlankFileName() {
        ObjectNameBuilder.build("release", " ");
    }
}
