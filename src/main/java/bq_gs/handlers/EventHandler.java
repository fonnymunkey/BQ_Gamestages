package bq_gs.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.entity.player.EntityPlayer;
import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_gs.tasks.*;

import java.util.List;

public class EventHandler
{
    @SubscribeEvent
    public void onGamestageAdded(GameStageEvent.Added event)
    {
        if(event.getEntityPlayer() == null || event.getEntityLiving().world.isRemote || event.isCanceled()) return;
        
		EntityPlayer player = event.getEntityPlayer();
        ParticipantInfo pInfo = new ParticipantInfo(player);
        
		List<DBEntry<IQuest>> actQuest = QuestingAPI.getAPI(ApiReference.QUEST_DB).bulkLookup(pInfo.getSharedQuests());
		
		for(DBEntry<IQuest> entry : actQuest)
		{
		    for(DBEntry<ITask> task : entry.getValue().getTasks().getEntries())
            {
                if(task.getValue() instanceof TaskGamestage) ((TaskGamestage)task.getValue()).checkStage(pInfo, entry);
            }
		}
    }
    
    @SubscribeEvent
    public void onGamestageRemoved(GameStageEvent.Removed event)
    {
        if(event.getEntityPlayer() == null || event.getEntityLiving().world.isRemote || event.isCanceled()) return;
        
		EntityPlayer player = event.getEntityPlayer();
        ParticipantInfo pInfo = new ParticipantInfo(player);
        
		List<DBEntry<IQuest>> actQuest = QuestingAPI.getAPI(ApiReference.QUEST_DB).bulkLookup(pInfo.getSharedQuests());
		
		for(DBEntry<IQuest> entry : actQuest)
		{
		    for(DBEntry<ITask> task : entry.getValue().getTasks().getEntries())
            {
                if(task.getValue() instanceof TaskGamestage) ((TaskGamestage)task.getValue()).checkStage(pInfo, entry);
            }
		}
    }
}