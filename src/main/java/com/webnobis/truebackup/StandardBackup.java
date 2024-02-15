package com.webnobis.truebackup;


import com.webnobis.truebackup.model.Commands;
import com.webnobis.truebackup.model.InvalidFile;
import com.webnobis.truebackup.read.Reader;
import com.webnobis.truebackup.read.StandardFileReader;
import com.webnobis.truebackup.repair.Repairer;
import com.webnobis.truebackup.repair.StandardFileRepairer;
import com.webnobis.truebackup.verify.StandardFileVerifier;
import com.webnobis.truebackup.verify.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record StandardBackup(List<Path> dirs, Reader<Path> reader, Verifier<InvalidFile<Long>, Path> verifier,
                             Repairer<InvalidFile<Long>> repairer)
        implements Backup<Long> {

    private static final Logger log = LoggerFactory.getLogger(StandardBackup.class);

    public StandardBackup(Commands commands) {
        this(toDirs(commands), new StandardFileReader(commands.firstSubDirFilterRegEx()), new StandardFileVerifier(), commands.repair() ? new StandardFileRepairer(commands.archiveDir()) : Stream::of);
    }

    private static List<Path> toDirs(Commands commands) {
        if (Objects.requireNonNull(commands.dirs(), "commands dirs is null").size() > 2) {
            log.warn("Standard backup only supports up to 2 dirs, but found {} dirs. The others were ignored. Backup started with dir {} as master.", commands.dirs().size(), commands.dirs().get(0));
            return commands.dirs().subList(0, 2);
        } else {
            if (Objects.requireNonNull(commands.dirs(), "commands dirs is null").size() == 2) {
                log.info("Standard backup started with dir {} as master.", commands.dirs().get(0));
            } else {
                log.info("Standard backup started.");
            }
            return commands.dirs();
        }
    }

}
