package bq_gs.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_gs.client.gui.PanelTaskGamestage;
import bq_gs.core.*;
import bq_gs.tasks.factory.FactoryTaskGamestage;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;
import java.lang.Math;

public class TaskGamestage implements ITask
{
	private final Set<UUID> completeUsers = new TreeSet<>(); 
	private final HashMap<UUID, Integer> userProgress = new HashMap<>();
	
	public boolean invert = false;
	public String name = "New Gamestage Name";
	public String gameStageName = "";
	
    @Override
    public ResourceLocation getFactoryID()
    {
        return FactoryTaskGamestage.INSTANCE.getRegistryName();
    }
    
    @Override
    public String getUnlocalisedName()
    {
        return BQGS.MODID + ".task.gamestage";
    }
    
	@Override
	public boolean isComplete(UUID uuid)
	{
		return completeUsers.contains(uuid);
	}
	
	@Override
	public void setComplete(UUID uuid)
	{
		completeUsers.add(uuid);
	}

	@Override
	public void resetUser(@Nullable UUID uuid)
	{
	    if(uuid == null)
        {
            completeUsers.clear();
            userProgress.clear();
        } else
        {
            completeUsers.remove(uuid);
            userProgress.remove(uuid);
        }
	}
	
    @Override
    public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest)
    {
        final List<Tuple<UUID, Integer>> progress = getBulkProgress(pInfo.ALL_UUIDS);
        
        progress.forEach((value) -> {
            if(value.getSecond() >= 1) setComplete(value.getFirst());
        });
        
		pInfo.markDirtyParty(Collections.singletonList(quest.getID()));
    }
    
    public void checkStage(ParticipantInfo pInfo, DBEntry<IQuest> quest)
    {
        if(GameStageHelper.hasStage(pInfo.PLAYER, gameStageName) == !invert) {
        	final List<Tuple<UUID, Integer>> progress = getBulkProgress(pInfo.ALL_UUIDS);
	            
            progress.forEach((value) -> {
            	if(isComplete(value.getFirst())) return;
            	int np = Math.min(1, value.getSecond() + 1);
            	setUserProgress(value.getFirst(), np);
            	if(np >= 1) setComplete(value.getFirst());
            });
            
            pInfo.markDirtyParty(Collections.singletonList(quest.getID()));
            return;
        }
    }
    
    @Override
    public synchronized NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setString("name", name);
        nbt.setString("gameStageName", gameStageName);
        nbt.setBoolean("invert", invert);
        return nbt;
    }
    
    @Override
    public synchronized void readFromNBT(NBTTagCompound nbt)
    {
        name = nbt.getString("name");
        gameStageName = nbt.getString("gameStageName");
        invert = nbt.getBoolean("invert");
    }
    
    @Override
	public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users)
	{
		NBTTagList jArray = new NBTTagList();
		NBTTagList progArray = new NBTTagList();
		
		if(users != null)
        {
            users.forEach((uuid) -> {
                if(completeUsers.contains(uuid)) jArray.appendTag(new NBTTagString(uuid.toString()));
                
                Integer data = userProgress.get(uuid);
                if(data != null)
                {
                    NBTTagCompound pJson = new NBTTagCompound();
                    pJson.setString("uuid", uuid.toString());
                    pJson.setInteger("value", data);
                    progArray.appendTag(pJson);
                }
            });
        } else
        {
            completeUsers.forEach((uuid) -> jArray.appendTag(new NBTTagString(uuid.toString())));
            
            userProgress.forEach((uuid, data) -> {
                NBTTagCompound pJson = new NBTTagCompound();
			    pJson.setString("uuid", uuid.toString());
                pJson.setInteger("value", data);
                progArray.appendTag(pJson);
            });
        }
		
		nbt.setTag("completeUsers", jArray);
		nbt.setTag("userProgress", progArray);
		
		return nbt;
	}
    
	@Override
	public void readProgressFromNBT(NBTTagCompound nbt, boolean merge)
	{
		if(!merge)
        {
            completeUsers.clear();
            userProgress.clear();
        }
		
		NBTTagList cList = nbt.getTagList("completeUsers", 8);
		for(int i = 0; i < cList.tagCount(); i++)
		{
			try
			{
				completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
			} catch(Exception e) {}
		}
		
		NBTTagList pList = nbt.getTagList("userProgress", 10);
		for(int n = 0; n < pList.tagCount(); n++)
		{
			try
			{
                NBTTagCompound pTag = pList.getCompoundTagAt(n);
                UUID uuid = UUID.fromString(pTag.getString("uuid"));
                userProgress.put(uuid, pTag.getInteger("value"));
			} catch(Exception e) {}
		}
	}
	
	private void setUserProgress(UUID uuid, Integer progress)
	{
		userProgress.put(uuid, progress);
	}
	
	public int getUsersProgress(UUID uuid)
	{
        Integer n = userProgress.get(uuid);
        return n == null? 0 : n;
	}
	
	private List<Tuple<UUID, Integer>> getBulkProgress(@Nonnull List<UUID> uuids)
    {
        if(uuids.size() <= 0) return Collections.emptyList();
        List<Tuple<UUID, Integer>> list = new ArrayList<>();
        uuids.forEach((key) -> list.add(new Tuple<>(key, getUsersProgress(key))));
        return list;
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest)
    {
        return new PanelTaskGamestage(rect, this);
    }
    
    @Override
    @Nullable
	@SideOnly(Side.CLIENT)
    public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest)
    {
        return null;
    }
}