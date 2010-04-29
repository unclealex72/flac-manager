// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NewMtpFile.java

package uk.co.unclealex.music.sync.service.mtp;

import uk.co.unclealex.music.base.model.DeviceFileBean;

// Referenced classes of package uk.co.unclealex.music.sync.service.mtp:
//            MtpFile

public class NewMtpFile extends MtpFile
{

    public NewMtpFile(DeviceFileBean deviceFileBean, String path, String filename)
    {
        super(deviceFileBean);
        i_path = path;
        i_filename = filename;
    }

    public String getPath()
    {
        return i_path;
    }

    public String getFilename()
    {
        return i_filename;
    }

    private String i_path;
    private String i_filename;
}
