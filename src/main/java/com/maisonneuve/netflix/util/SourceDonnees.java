package com.maisonneuve.netflix.util;

import com.maisonneuve.netflix.model.Media;
import java.util.List;

public interface SourceDonnees {

    List<Media> chargerDonnees();
}