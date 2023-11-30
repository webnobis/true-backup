package com.webnobis.truebackup;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BackupFactoryTest {

    private BackupFactory factory;

    @BeforeAll
    static void setUpAll() {
        BackupFactory.factoryRef.set(new BackupFactory());
    }

    @BeforeEach
    void setUp() {
        factory = BackupFactory.instance();
    }

    @Test
    void instance() {
        assertSame(factory, BackupFactory.instance());
    }

    @Test
    void ofDefault() {
        assertEquals(Backup.class, factory.of(null, null, true, null).getClass());
    }

    @Test
    void ofFullFailed() {
        assertThrows(UnsupportedOperationException.class, () -> factory.of(null, true, null, null));
    }
}