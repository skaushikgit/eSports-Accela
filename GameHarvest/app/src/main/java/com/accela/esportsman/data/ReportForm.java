package com.accela.esportsman.data;

import com.accela.mobile.AMLogger;
import com.accela.record.model.RecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by jzhong on 9/8/15.
 */
public class ReportForm implements Serializable {
    public int tagInt;
    private RecordModel tag;
    //location
    public String county;
    public String town;
    public String wmu;

    //harvest method
    public String dateOfKill;
    public String season;
    public String methodUsed;

    //havest species
    //- bear and dear
    public String sex;
    // - bear
    public String age;
    public String examinationCounty;
    public String address;
    public String contactPhone;
    // - deer
    public String leftAntlerPoints;
    public String rightAntlerPoints;
    // - spring/fall turky
    public String weight;
    public String legSaved; //only for fall turkey
    public String beardLength;
    public String spurLength;

    public ReportForm(RecordModel tag) {
        this.tag = tag;
        tagInt = DataManager.getAnimalTagInt(tag);
    }


    private boolean isFieldFilled(String field) {
        return field != null && field.length()>0;
    }

    public boolean isReadyForSubmit() {
        if(!isHarvestSpeciesFormFilled()) {
            return false;
        }
        if (!isHarvestLocationFormFilled()) {
            return false;
        }
        if (!isHarvestMethodFormFilled()) {
            return false;
        }
        return true;
    }

    public boolean isTurkeyReadyForSubmit() {
        if(!isHarvestSpeciesFormFilled()) {
            return false;
        }
        if (!isHarvestLocationFormFilled()) {
            return false;
        }
        return true;
    }

    public boolean isHarvestLocationFormFilled() {
        String[] fields = new String[] {
                county,
                town,
                wmu
        };
        for (String field : fields) {
            if (!isFieldFilled(field)) {
                return false;
            }
        }
        return true;
    }

    public boolean isHarvestMethodFormFilled() {
        String[] fields = new String[] {
                dateOfKill,
                season,
                methodUsed
        };
        if(fields!=null) {
            for(String field: fields) {
                if(!isFieldFilled(field)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isHarvestSpeciesFormFilled() {

        String[] fields = null;
        if(tagInt == DataManager.ANIMAL_TAG_BEAR) {
            fields = new String[]{
                    sex,
                    age
            };

        } else if(tagInt == DataManager.ANIMAL_TAG_DEER) {
            fields = new String[]{
                    sex,
                    leftAntlerPoints,
                    rightAntlerPoints
            };

        } else if(tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY) {
            fields = new String[]{
                    weight,
                    beardLength,
                    spurLength
            };
        } else if(tagInt == DataManager.ANIMAL_TAG_FALL_TURKEY) {
            fields = new String[]{
                    weight,
                    legSaved,
                    beardLength,
                    spurLength
            };
        }
        if(fields!=null) {
            for(String field: fields) {
                if(!isFieldFilled(field)) {
                    return false;
                }
            }
        }

        return true;
    }

    public JSONArray buildJSONForSubmit(String dateOfBirth) {
        JSONArray jsonPost = new JSONArray();
        try {
            JSONObject asi1 = new JSONObject();
            asi1.put("id", "HARVEST_MST-OTHER.cINFORMATION");
            asi1.put("County for Examination of Bear", examinationCounty);
            asi1.put("Address for Examination", address);
            asi1.put("Contact Phone #", contactPhone);
            asi1.put("Reporting Channel", "Mobile Device");

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Long daysKilled = 0l;
            try {

                Date killDate = formatter.parse(dateOfKill);
                long diffInMillies = new Date().getTime() - killDate.getTime();
                daysKilled = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            } catch (ParseException e) {
                AMLogger.logError(e.toString());
            }

            asi1.put("Number of days hunted to kill this turkey",
                    (tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY || tagInt == DataManager.ANIMAL_TAG_FALL_TURKEY)? daysKilled.toString(): null); //Wait for confirm how to set it
            jsonPost.put(asi1);

            JSONObject asi2 = new JSONObject();
            asi2.put("id", "HARVEST_MST-SELECTED.cTAG");
            asi2.put("TAG ID to Report On", tag.getCustomId());
            jsonPost.put(asi2);

            JSONObject asi3 = new JSONObject();
            asi3.put("id", "HARVEST_MST-KILL.cINFORMATION");
            asi3.put("Age", age );
            asi3.put("Date of Kill", dateOfKill);
            asi3.put("Turkey Spur Length", spurLength);
            asi3.put("Bear Season", tagInt == DataManager.ANIMAL_TAG_BEAR? season : null);
            asi3.put("County of Kill", county);
            asi3.put("Right Antler Points", rightAntlerPoints);
            asi3.put("WMU", wmu);
            asi3.put("Turkey Beard Length", beardLength);
            asi3.put("Weight (to nearest pound)", weight);
            asi3.put("Turkey Leg Saved?", legSaved);
            asi3.put("Left Antler Points", leftAntlerPoints);
            asi3.put("Town", town);
            asi3.put("Deer Season", tagInt == DataManager.ANIMAL_TAG_DEER? season : null);
            asi3.put("Turkey Season", tagInt == DataManager.ANIMAL_TAG_FALL_TURKEY? season : null);
            asi3.put("Turkey Season", tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY? season : null);
            asi3.put("Bear Taken With", tagInt == DataManager.ANIMAL_TAG_BEAR? methodUsed: null);
            asi3.put("Deer Taken With", tagInt == DataManager.ANIMAL_TAG_DEER? methodUsed: null);
            asi3.put("Turkey Taken With", tagInt == DataManager.ANIMAL_TAG_FALL_TURKEY? methodUsed: null);
            asi3.put("Turkey Taken With", tagInt == DataManager.ANIMAL_TAG_SPRING_TURKEY? methodUsed: null);
            asi3.put("Sex", sex);
            jsonPost.put(asi3);

            JSONObject asi4 = new JSONObject();
            asi4.put("id", "HARVEST_MST-TAG.cINFORMATION");
            asi4.put("DEC Cust. ID", null);
            asi4.put("Carcass Tag ID", tag.getCustomId());
            switch (tagInt) {
                case DataManager.ANIMAL_TAG_BEAR:
                    asi4.put("Harvest Type", "Bear Report");
                    break;
                case DataManager.ANIMAL_TAG_DEER:
                    asi4.put("Harvest Type", "Deer Report");
                    break;
                case DataManager.ANIMAL_TAG_FALL_TURKEY:
                    asi4.put("Harvest Type", "Fall Turkey Report");
                    break;
                case DataManager.ANIMAL_TAG_SPRING_TURKEY:
                    asi4.put("Harvest Type", "Spring Turkey Report");
                    break;
            }
            if (tag.getName() != null) {
                asi4.put("Are you reporting on a consigned DMP tag?", tag.getName().contains("DMP") ? "YES" : "No"); //?????? Wait for confirm how to set it
            }
            asi4.put("Date Of Birth", dateOfBirth);  // ????? Wait for confirm how to set it
            jsonPost.put(asi4);

        } catch (JSONException e) {
            AMLogger.logError(e.toString());
        }
        return jsonPost;
    }

}
