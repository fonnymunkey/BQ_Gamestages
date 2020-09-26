package bq_gs.tasks.factory;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.registry.IFactoryData;
import bq_gs.core.BQGS;
import bq_gs.tasks.TaskGamestage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class FactoryTaskGamestage implements IFactoryData<ITask, NBTTagCompound>
{
	public static final FactoryTaskGamestage INSTANCE = new FactoryTaskGamestage();
	
	@Override
	public ResourceLocation getRegistryName()
	{
		return new ResourceLocation(BQGS.MODID + ":gamestage");
	}

	@Override
	public TaskGamestage createNew()
	{
		return new TaskGamestage();
	}

	@Override
	public TaskGamestage loadFromData(NBTTagCompound json)
	{
		TaskGamestage task = new TaskGamestage();
		task.readFromNBT(json);
		return task;
	}
	
}