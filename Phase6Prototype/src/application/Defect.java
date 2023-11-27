package application;

// class for Defects
public class Defect {
	private int DefectID;
	private int ProjID;
	private String ProjName;
	private String DefectName;
	private String DefectDesc;
	private String DefectStatus;
	private String InjectedStep;
	private String RemovedStep;
	private String DefectCategory;
	private String DefectOrigin;
	
//	Defect constructor
	public Defect(int Did, int id, String ProjName, String DefectName, String DefectDesc, String DefectStatus,
			String InjectedStep, String RemovedStep, String DefectCategory, String DefectOrigin) {
		this.DefectID = Did;
		this.ProjID = id;
		this.ProjName = ProjName;
		this.DefectName = DefectName;
		this.DefectDesc = DefectDesc;
		this.DefectStatus = DefectStatus;
		this.InjectedStep = InjectedStep;
		this.RemovedStep = RemovedStep;
		this.DefectCategory = DefectCategory;
		this.DefectOrigin = DefectOrigin;
	}
	
//	Getters and Setters for Defect properties
	public int GetDefectID() {
		return DefectID;
	}
	
	public int GetID() {
		return ProjID;
	}
	
	public String GetProjName() {
		return ProjName;
	}
	
	public String GetDefectName() {
		return DefectName;
	}
	
	public void SetDefectName(String newName) {
		DefectName = newName;
	}
	
	public String GetDefectDesc() {
		return DefectDesc;
	}
	
	public void SetDefectDesc(String newDesc) {
		DefectDesc = newDesc;
	}
	public String GetDefectStatus() {
		return DefectStatus;
	}
	public void SetDefectStatus(String newStatus) {
		DefectStatus = newStatus;
	}
	public String GetInjectedStep() {
		return InjectedStep;
	}
	public void SetInjectedStep(String newStep) {
		InjectedStep = newStep;
	}
	public String GetRemovedStep() {
		return RemovedStep;
	}
	public void SetRemovedStep(String newStep) {
		RemovedStep = newStep;
	}
	public String GetDefectCategory() {
		return DefectCategory;
	}
	public void SetDefectCategory(String newCat) {
		DefectCategory = newCat;
	}
	public String GetDefectOrigin() {
		return DefectOrigin;
	}
	public void SetDefectOrigin(String origin) {
		DefectOrigin = origin;
	}
}
