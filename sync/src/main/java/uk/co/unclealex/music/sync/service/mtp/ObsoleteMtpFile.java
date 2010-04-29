// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ObsoleteMtpFile.java

package uk.co.unclealex.music.sync.service.mtp;

import uk.co.unclealex.music.base.model.DeviceFileBean;

// Referenced classes of package uk.co.unclealex.music.sync.service.mtp:
//            MtpFile

public class ObsoleteMtpFile extends MtpFile
{

    public ObsoleteMtpFile(DeviceFileBean deviceFileBean, String id)
    {
        super(deviceFileBean);
        i_id = id;
    }

    public String getId()
    {
        return i_id;
    }

    private String i_id;
}
