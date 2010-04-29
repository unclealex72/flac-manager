// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MtpFile.java

package uk.co.unclealex.music.sync.service.mtp;

import uk.co.unclealex.music.base.model.DeviceFileBean;

public abstract class MtpFile
{

    protected MtpFile(DeviceFileBean deviceFileBean)
    {
        i_artistCode = deviceFileBean.getArtistCode();
        i_albumCode = deviceFileBean.getAlbumCode();
        i_trackNumber = deviceFileBean.getTrackNumber();
        i_trackCode = deviceFileBean.getTrackCode();
    }

    public String getArtistCode()
    {
        return i_artistCode;
    }

    public String getAlbumCode()
    {
        return i_albumCode;
    }

    public int getTrackNumber()
    {
        return i_trackNumber;
    }

    public String getTrackCode()
    {
        return i_trackCode;
    }

    private String i_artistCode;
    private String i_albumCode;
    private int i_trackNumber;
    private String i_trackCode;
}
