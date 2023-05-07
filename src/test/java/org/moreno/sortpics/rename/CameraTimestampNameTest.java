package org.moreno.sortpics.rename;

import org.junit.jupiter.api.Test;

class CameraTimestampNameTest {

    @Test
    void getDateFromData() {
        var fileName = "(2017-11-26)[17.22.46]-LastModifiedDate[47](VID_20170622_WA0020).mp4";
        var expected = "(2017-06-22)[17.22.46]-LastModifiedDate[47](VID_20170622_WA0020).mp4";
        var actual = CameraTimestampName.getDateFromData(fileName);
        assert actual.equals(expected);
    }
}