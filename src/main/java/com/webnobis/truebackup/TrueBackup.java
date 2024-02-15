package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Commands;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

public record TrueBackup(BackupFactory backupFactory) {

    private static final int ONE = 1;

    private static final Logger log = LoggerFactory.getLogger(TrueBackup.class);

    /**
     * Executes backup service, depending on commands
     *
     * @param args command arguments
     */
    public static void main(String[] args) {
        new TrueBackup(new BackupFactory()).backup(args);
    }

    private static Commands transform(String[] args) {
        Options options = getOptions();
        if (Optional.ofNullable(args).filter(array -> array.length > 0).isEmpty()) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(TrueBackup.class.getSimpleName(), Options.class.getSimpleName(), options, """
                                        
                    Workflow
                    1. verify
                    2. repair, if -r
                                        
                    Made by Steffen Nobis""");
        } else {
            CommandLineParser parser = new DefaultParser();
            try {
                CommandLine commands = parser.parse(options, args);
                log.debug("commands read");
                return new Commands(Arrays.stream(commands.getOptionValues('b')).map(Path::of).toList(), commands.hasOption('r'), Path.of(commands.getOptionValue('a')), commands.getOptionValue('f'));
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("r", "repair", false, "repair invalid files after verify");
        Option copy = new Option("b", "backup-directories", true, "All root directories to verify and repair if repair flag is set. Standard backup only supports up to 2 dirs.");
        copy.setArgs(Option.UNLIMITED_VALUES);
        copy.setRequired(true);
        options.addOption(copy);
        Option archive = new Option("a", "archive-directory", true, "Archive root directory, only used repair flag is set and the file should be removed");
        archive.setArgs(ONE);
        archive.setRequired(false);
        options.addOption(archive);
        Option subdirRegex = new Option("s", "sub-directory-regex", true, "optional first level sub directory filter regex, only used if full backup");
        archive.setArgs(ONE);
        archive.setRequired(false);
        options.addOption(subdirRegex);
        return options;
    }

    public void backup(String[] args) {
        Commands commands = transform(args);
        backupFactory().createBackup(commands).backup();
    }

}
