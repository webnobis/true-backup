package com.webnobis.truebackup;

import org.junit.jupiter.api.extension.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

public class TempDirExtension implements BeforeEachCallback, BeforeAllCallback, AfterEachCallback, AfterAllCallback, ParameterResolver {

    private Path dirAll;

    private Path dirEach;

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        deleteTempDirectory(dirAll);
    }

    private void deleteTempDirectory(Path directory) throws IOException {
        if (directory != null) {
            Files.walkFileTree(directory, new FileVisitor<>() {
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
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        deleteTempDirectory(dirEach);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        dirAll = getTempDirectory(extensionContext);
    }

    private Path getTempDirectory(ExtensionContext extensionContext) throws IOException {
        return Files.createTempDirectory(extensionContext.getRequiredTestClass().getSimpleName());
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        dirEach = getTempDirectory(extensionContext);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return Path.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (supportsParameter(parameterContext, extensionContext)) {
            return Optional.ofNullable(dirEach).orElse(dirAll);
        }
        throw new ParameterResolutionException("expected " + Path.class);
    }
}
