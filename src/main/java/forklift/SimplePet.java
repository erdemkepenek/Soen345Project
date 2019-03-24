package forklift;
import java.time.LocalDate;
import java.util.Date;
import org.springframework.samples.petclinic.owner.*;

public class SimplePet {
	
	private int id;
	private String name;
	private Date birth_date;
	private int type_id;
	private int owner_id;
	
	
	
	public int getId() {
        return this.id;
    }
	
	public void setId(int id) {
		this.id=id;
    }
	
	public String getName() {
        return this.name;
    }
	
	public void setName(String name) {
		this.name=name;
    }
	
	public Date getDate() {
        return this.birth_date;
    }
	
	public void setDate(Date birth_date) {
        this.birth_date=birth_date;
    }
	
	public int getType() {
        return this.type_id;
    }
	
	public void setType(int type_id ) {
        this.type_id=type_id;
    }
	
	public int getOwner() {
        return this.owner_id;
    }
	
	public void setOwner(int owner_id) {
        this.owner_id=owner_id;
    }
	
	

}
