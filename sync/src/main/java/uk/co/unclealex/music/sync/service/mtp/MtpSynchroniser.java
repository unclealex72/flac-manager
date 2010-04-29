// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MtpSynchroniser.java

package uk.co.unclealex.music.sync.service.mtp;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections15.*;
import uk.co.unclealex.music.base.RequiresPackages;
import uk.co.unclealex.music.base.model.DeviceFileBean;
import uk.co.unclealex.music.base.process.service.ProcessCallback;
import uk.co.unclealex.music.base.service.*;
import uk.co.unclealex.music.sync.service.AbstractSynchroniser;

// Referenced classes of package uk.co.unclealex.music.sync.service.mtp:
//            NewMtpFile, ObsoleteMtpFile

public class MtpSynchroniser extends AbstractSynchroniser
    implements RequiresPackages
{

    public MtpSynchroniser()
    {
    }

    public void doPostConstruct()
    {
        Transformer newMtpFileTransformer = new Transformer() {

            public NewMtpFile transform(java.util.Map.Entry entry)
            {
                DeviceFileBean deviceFileBean = (DeviceFileBean)entry.getKey();
                File file = (File)entry.getValue();
                return new NewMtpFile(deviceFileBean, file.getAbsolutePath(), file.getName());
            }

            public volatile Object transform(Object x0)
            {
                return transform((java.util.Map.Entry)x0);
            }

            final MtpSynchroniser this$0;

            
            {
                this$0 = MtpSynchroniser.this;
                super();
            }
        };
        Transformer obsoleteMtpFileTransformer = new Transformer() {

            public ObsoleteMtpFile transform(DeviceFileBean deviceFileBean)
            {
                return new ObsoleteMtpFile(deviceFileBean, deviceFileBean.getDeviceFileId());
            }

            public volatile Object transform(Object x0)
            {
                return transform((DeviceFileBean)x0);
            }

            final MtpSynchroniser this$0;

            
            {
                this$0 = MtpSynchroniser.this;
                super();
            }
        };
        setNewMtpFileTransformer(newMtpFileTransformer);
        setObsoleteMtpFileTransformer(obsoleteMtpFileTransformer);
        setPythonPattern(Pattern.compile("(ADD|DELETE): (.+?),(.+?),(.+?),([0-9]+),(.+?)"));
    }

    public void synchronise(final SortedSet deviceFilesToDelete, final SortedMap deviceFilesToAdd)
        throws SynchronisationException
    {
        Set allDeviceFileBeans = new HashSet();
        allDeviceFileBeans.addAll(deviceFilesToDelete);
        allDeviceFileBeans.addAll(deviceFilesToAdd.keySet());
        Map model = new HashMap();
        List obsoleteTracks = (List)CollectionUtils.collect(deviceFilesToDelete, getObsoleteMtpFileTransformer(), new LinkedList());
        List newTracks = (List)CollectionUtils.collect(deviceFilesToAdd.entrySet(), getNewMtpFileTransformer(), new LinkedList());
        model.put("obsoleteTracks", obsoleteTracks);
        model.put("newTracks", newTracks);
        final Pattern pythonPattern = getPythonPattern();
        ProcessCallback callback = new ProcessCallback() {

            public void lineWritten(String line)
            {
                line = line.trim();
                Matcher matcher = pythonPattern.matcher(line);
                if(matcher.matches())
                {
                    String command = matcher.group(1);
                    String artistCode = matcher.group(2);
                    String albumCode = matcher.group(3);
                    int trackNumber = Integer.valueOf(matcher.group(4)).intValue();
                    String trackCode = matcher.group(5);
                    String id = matcher.group(6);
                    if("ADD".equals(command))
                        executeAdd(artistCode, albumCode, trackNumber, trackCode, id, deviceFilesToAdd.keySet());
                    else
                        executeDelete(artistCode, albumCode, trackNumber, trackCode, id, deviceFilesToDelete);
                }
            }

            public void errorLineWritten(String s)
            {
            }

            final Pattern val$pythonPattern;
            final SortedMap val$deviceFilesToAdd;
            final SortedSet val$deviceFilesToDelete;
            final MtpSynchroniser this$0;

            
            {
                this$0 = MtpSynchroniser.this;
                pythonPattern = pattern;
                deviceFilesToAdd = sortedmap;
                deviceFilesToDelete = sortedset;
                super();
            }
        };
        try
        {
            getPythonScriptService().runCommand("mtp-sync", callback, model);
        }
        catch(ScriptException e)
        {
            throw new SynchronisationException(e);
        }
        catch(IOException e)
        {
            throw new SynchronisationException(e);
        }
    }

    protected void executeAdd(final String artistCode, final String albumCode, final int trackNumber, final String trackCode, String id, Set deviceFileBeans)
    {
        Predicate predicate = new Predicate() {

            public boolean evaluate(DeviceFileBean deviceFileBean)
            {
                return deviceBeanHasCodes(deviceFileBean, artistCode, albumCode, trackNumber, trackCode);
            }

            public volatile boolean evaluate(Object x0)
            {
                return evaluate((DeviceFileBean)x0);
            }

            final String val$artistCode;
            final String val$albumCode;
            final int val$trackNumber;
            final String val$trackCode;
            final MtpSynchroniser this$0;

            
            {
                this$0 = MtpSynchroniser.this;
                artistCode = s;
                albumCode = s1;
                trackNumber = i;
                trackCode = s2;
                super();
            }
        };
        DeviceFileBean deviceFileBean = (DeviceFileBean)CollectionUtils.find(deviceFileBeans, predicate);
        if(deviceFileBean != null)
        {
            deviceFileBean.setDeviceFileId(id);
            getSynchroniserCallback().trackAdded(((MtpConnectedDevice)getConnectedDevice()).getDeviceBean(), deviceFileBean);
        }
    }

    protected void executeDelete(final String artistCode, final String albumCode, final int trackNumber, final String trackCode, final String id, SortedSet deviceFileBeans)
    {
        Predicate predicate = new Predicate() {

            public boolean evaluate(DeviceFileBean deviceFileBean)
            {
                return deviceBeanHasCodes(deviceFileBean, artistCode, albumCode, trackNumber, trackCode) && id.equals(deviceFileBean.getDeviceFileId());
            }

            public volatile boolean evaluate(Object x0)
            {
                return evaluate((DeviceFileBean)x0);
            }

            final String val$artistCode;
            final String val$albumCode;
            final int val$trackNumber;
            final String val$trackCode;
            final String val$id;
            final MtpSynchroniser this$0;

            
            {
                this$0 = MtpSynchroniser.this;
                artistCode = s;
                albumCode = s1;
                trackNumber = i;
                trackCode = s2;
                id = s3;
                super();
            }
        };
        DeviceFileBean deviceFileBean = (DeviceFileBean)CollectionUtils.find(deviceFileBeans, predicate);
        if(deviceFileBean != null)
            getSynchroniserCallback().trackRemoved(((MtpConnectedDevice)getConnectedDevice()).getDeviceBean(), deviceFileBean);
    }

    protected boolean deviceBeanHasCodes(DeviceFileBean deviceFileBean, String artistCode, String albumCode, int trackNumber, String trackCode)
    {
        return artistCode.equals(deviceFileBean.getArtistCode()) && albumCode.equals(deviceFileBean.getAlbumCode()) && trackNumber == deviceFileBean.getTrackNumber() && trackCode.equals(deviceFileBean.getTrackCode());
    }

    public String[] getRequiredPackageNames()
    {
        return (new String[] {
            "python-pymtp", "python-eyed3"
        });
    }

    public ScriptService getPythonScriptService()
    {
        return i_pythonScriptService;
    }

    public void setPythonScriptService(ScriptService pythonScriptService)
    {
        i_pythonScriptService = pythonScriptService;
    }

    public Transformer getNewMtpFileTransformer()
    {
        return i_newMtpFileTransformer;
    }

    public void setNewMtpFileTransformer(Transformer newMtpFileTransformer)
    {
        i_newMtpFileTransformer = newMtpFileTransformer;
    }

    public Transformer getObsoleteMtpFileTransformer()
    {
        return i_obsoleteMtpFileTransformer;
    }

    public void setObsoleteMtpFileTransformer(Transformer obsoleteMtpFileTransformer)
    {
        i_obsoleteMtpFileTransformer = obsoleteMtpFileTransformer;
    }

    public Pattern getPythonPattern()
    {
        return i_pythonPattern;
    }

    public void setPythonPattern(Pattern pythonPattern)
    {
        i_pythonPattern = pythonPattern;
    }

    private ScriptService i_pythonScriptService;
    private Transformer i_newMtpFileTransformer;
    private Transformer i_obsoleteMtpFileTransformer;
    private Pattern i_pythonPattern;
}
