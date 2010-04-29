// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MountPointSynchroniser.java

package uk.co.unclealex.music.sync.service.mount;

import java.util.*;
import uk.co.unclealex.music.base.RequiresPackages;
import uk.co.unclealex.music.base.service.SynchronisationException;
import uk.co.unclealex.music.sync.service.AbstractSynchroniser;

public class MountPointSynchroniser extends AbstractSynchroniser
    implements RequiresPackages
{

    public MountPointSynchroniser()
    {
    }

    public void doPostConstruct()
    {
    }

    public String[] getRequiredPackageNames()
    {
        List requiredPackageNames = new ArrayList(Arrays.asList(getExtraRequiredPackageNames()));
        requiredPackageNames.add("pmount");
        return (String[])requiredPackageNames.toArray(new String[0]);
    }

    protected String[] getExtraRequiredPackageNames()
    {
        return new String[0];
    }

    public void synchronise(SortedSet sortedset, SortedMap sortedmap)
        throws SynchronisationException
    {
    }
}
