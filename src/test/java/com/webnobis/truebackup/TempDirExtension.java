package com.webnobis.truebackup;

import org.junit.jupiter.api.extension.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public class TempDirExtension implements AfterAllCallback, AfterEachCallback, BeforeAllCallback, BeforeEachCallback, ParameterResolver {

    private Path tmpDirAll;

    private Path tmpDirEach;

    private static void delete(Path dir) {
        try {
            Files.walkFileTree(dir, new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        Optional.ofNullable(tmpDirAll).ifPresent(TempDirExtension::delete);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        Optional.ofNullable(tmpDirEach).ifPresent(TempDirExtension::delete);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        tmpDirAll = Files.createTempDirectory(extensionContext.getRequiredTestClass().getSimpleName());
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        tmpDirEach = Files.createTempDirectory(extensionContext.getRequiredTestClass().getSimpleName());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return Path.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (Path.class.isAssignableFrom(parameterContext.getParameter().getType())) {
            return Optional.ofNullable(tmpDirEach).orElse(tmpDirAll);
        } else {
            return null;
        }
    }

}
