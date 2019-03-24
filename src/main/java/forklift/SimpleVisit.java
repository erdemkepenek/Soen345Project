package forklift;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.samples.petclinic.owner.*;

public class SimpleVisit {

	private int id;
	private int pet_id;
	private Date visit_date;
	private String description;
	
	SimpleVisit(){}
	
	SimpleVisit(int id, int pet_id, Date visit_date, String description) {
		this.id=id;
		this.pet_id=pet_id;
		this.visit_date=visit_date;
		this.description=description;
	}
	
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
	
    
}
