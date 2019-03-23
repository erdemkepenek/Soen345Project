package forklift;


import org.springframework.samples.petclinic.owner.Owner;

import java.sql.ResultSet;

public class ShadowRead {

    private static ShadowRead shadowRead;
    private static OwnerCRUD ownerCRUD = new OwnerCRUD();

    private ShadowRead (){}

    public ShadowRead getInstance(){
        if(shadowRead == null) {
            shadowRead = new ShadowRead();
        }
        return shadowRead;
    }

    public static Owner findOwnerByID(Integer id) {
        return ownerCRUD.selectOwnerById(id);
    }

    public static Owner findOwnerByLastName(String lastName) {
        return ownerCRUD.selectOwnerByLastName(lastName);
    }


}
