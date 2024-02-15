package com.webnobis.truebackup.model;

import java.nio.file.Path;
import java.util.List;

public record Commands(List<Path> dirs, boolean repair, Path archiveDir, String firstSubDirFilterRegEx) {


}
