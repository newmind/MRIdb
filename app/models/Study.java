package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.GenericModel;

@Entity
//@NamedNativeQuery(name = "nativeSQL", query = "select * from study", resultClass = Study.class)
public class Study extends GenericModel {

	@Id
	public long pk;
	public String study_id;
	public String study_desc;
	public Date study_datetime;
	public String study_custom1;
	@ManyToOne
	@JoinColumn(name="patient_fk")
	public Patient patient;
	@OneToMany(mappedBy = "study")
	public Set<Series> series;

	@Override
	public String toString() {
		return String.format("%s %s %s %s %s", pk, study_desc, study_datetime, study_custom1, patient);
	}
}