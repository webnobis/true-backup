package com.webnobis.truebackup.test.extensions;

import com.webnobis.truebackup.Backup;
import com.webnobis.truebackup.model.Commands;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.Reader;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.verify.Verifier;

import java.nio.file.Path;
import java.util.List;

public record TestExtensionsBackup(Commands commands) implements Backup<Void> {

    @Override
    public List<Path> dirs() {
        return null;
    }

    @Override
    public Reader<Path> reader() {
        return null;
    }

    @Override
    public Verifier<InvalidFile<Void>, Path> verifier() {
        return null;
    }

    @Override
    public Repairer<InvalidFile<Void>> repairer() {
        return null;
    }
}