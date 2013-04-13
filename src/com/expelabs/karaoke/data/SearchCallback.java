package com.expelabs.karaoke.data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 13.04.13
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public interface SearchCallback{
    void onFound(List<TrackEntry> result);
}