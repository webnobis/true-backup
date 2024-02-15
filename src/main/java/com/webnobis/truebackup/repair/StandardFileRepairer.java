package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.verify.StandardFileVerifier;

import java.io.IOException;
import java.nio.file.Path;

public class StandardFileRepairer extends AbstractFileRepairer<Long> {

    public StandardFileRepairer(Path archiveDir) {
        super(archiveDir, new StandardFileVerifier());
    }

    @Override
    protected void repair(Path invalidFile, Path validFile, Long invalidBytes) throws IOException {
        copy(validFile, invalidFile);
    }
}
