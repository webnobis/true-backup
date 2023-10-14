package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.model.InvalidFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Default repairer of invalid files
 *
 * @author Steffen Nobis
 */
public class DefaultRepairer extends AbstractRepairer {

    private static final Logger log = LoggerFactory.getLogger(DefaultRepairer.class);

    /**
     * Delete flag setting constructor
     *
     * @param archiveDirForInvalidFileIfItShouldNotExist if valid file doesn't exist, moves the invalid file to archive dir or if null deletes the invalid file
     * @see AbstractRepairer#AbstractRepairer(java.nio.file.Path)
     */
    public DefaultRepairer(Path archiveDirForInvalidFileIfItShouldNotExist) {
        super(archiveDirForInvalidFileIfItShouldNotExist);
    }

    @Override
    protected void repairBytes(InvalidFile invalidFile) throws IOException {
        create(invalidFile);
        log.info("invalid file {} repaired", invalidFile);
    }
}
