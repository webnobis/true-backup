package com.webnobis.truebackup;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(TempDirExtension.class)
class TempDirExtensionTest {

    private static Path dirAll;

    private Path dirEach;

    @BeforeAll
    static void setUpAll(Path dir) {
        dirAll = dir;
    }

    @BeforeEach
    void setUp(Path dir) {
        dirEach = dir;
        assertNotEquals(dirAll, dirEach);
    }

    @Test
    void resolveParameter(Path dir) {
        assertEquals(dirEach, dir);
        assertNotEquals(dirAll, dir);
    }
}