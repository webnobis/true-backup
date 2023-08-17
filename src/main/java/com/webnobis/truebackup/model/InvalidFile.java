package com.webnobis.truebackup.model;

import java.nio.file.Path;
import java.util.List;

public record InvalidFile(Path file, List<InvalidByte> invalidBytes) {

}
