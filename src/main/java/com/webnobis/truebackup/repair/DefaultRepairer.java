package com.webnobis.truebackup.repair;

import com.webnobis.truebackup.model.InvalidFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
     * @see AbstractRepairer#AbstractRepairer(boolean)
     */
    public DefaultRepairer(boolean deleteInvalidFileIfItShouldNotExist) {
        super(deleteInvalidFileIfItShouldNotExist);
    }

    @Override
    protected void repairBytes(InvalidFile invalidFile) throws IOException {
        create(invalidFile);
        log.info("invalid file {} repaired", invalidFile);
    }
}
