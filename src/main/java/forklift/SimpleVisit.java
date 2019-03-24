package forklift;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.samples.petclinic.owner.*;

public class SimpleVisit {

	private int id;
	private int pet_id;
	private Date visit_date;
	private String description;
	
	
	
	public int getId() {
        return this.id;
    }
	
	public int getPetId() {
        return this.pet_id;
    }
	public Date getDate() {
        return this.visit_date;
    }
	
	public String getDescription() {
        return this.description;
    }
	
	public void setId(int id) {
		this.id=id;
    }
	
	public void setPetId(int pet_id) {
		this.pet_id=pet_id;
    }
	public void setDate(Date visit_date) {
		this.visit_date=visit_date;
    }
	
	public void setDescription(String description) {
		this.description=description;
    }
	
	
    
}
