package com.webnobis.truebackup;

import com.webnobis.truebackup.model.Commands;

@FunctionalInterface
public interface BackupBuilder {

    Backup<?> createBackup(Commands commands);
}
