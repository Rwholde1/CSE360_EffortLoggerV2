package application;

public class EffortLog {
	private String ProjName;
	private String LifeCycleStep;
	private String EffortCategory;
	private String SubEffortCategory;
	private String StartTime;
	private String EndTime;
	
	public EffortLog(String ProjName, String LifeCycleStep, String EffortCategory, String SubEffortCategory, String StartTime, String EndTime)
	{
		this.ProjName = ProjName;
		this.LifeCycleStep = LifeCycleStep;
		this.EffortCategory = EffortCategory;
		this.SubEffortCategory = SubEffortCategory;
		this.StartTime = StartTime;
		this.EndTime = EndTime;
	}
	
	public String getName()
	{
		return this.ProjName;
	}
	
	public String getLifeCycleStep()
	{
		return this.LifeCycleStep;
	}
	
	public String getEffortCategory()
	{
		return this.EffortCategory;
	}
	
	public String getSubEffortCategory()
	{
		return this.SubEffortCategory;
	}
	
	public String getStartTime()
	{
		return this.StartTime;
	}
	
	public String getEndTime()
	{
		return this.EndTime;
	}
}
