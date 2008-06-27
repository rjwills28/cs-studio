package org.csstudio.nams.configurator.beans;

import java.beans.PropertyChangeSupport;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;

public class AlarmbearbeiterBean extends
		AbstractConfigurationBean<AlarmbearbeiterBean> {

	public static enum PropertyNames {
		userID, confirmCode, name, email, mobilePhone, phone, statusCode, active, preferedAlarmType

	}

	private int userID;// PRIMARY KEY
	private String name = "";
	private String email = "";
	private String mobilePhone = "";
	private String phone = "";
	private String statusCode = "";
	private String confirmCode = "";
	private boolean isActive = false;
	private PreferedAlarmType preferedAlarmType = PreferedAlarmType.NONE;


	public AlarmbearbeiterBean() {
		userID = -1;
	}

	public int getUserID() {
		return userID;
	}

	/**
	 * Shall not be used in user generated code. The UserID is an autogenerated
	 * value.
	 */
	public void setUserID(int userID) {
		int oldValue = getUserID();
		this.userID = userID;
		pcs.firePropertyChange(PropertyNames.userID.name(),
				oldValue, getUserID());
	}

	public String getConfirmCode() {
		return confirmCode;
	}

	public void setConfirmCode(String confirmCode) {
		String oldValue = getConfirmCode();
		this.confirmCode = confirmCode;
		pcs.firePropertyChange(PropertyNames.confirmCode
				.name(), oldValue, getConfirmCode());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = getName();
		this.name = name;
		pcs.firePropertyChange(PropertyNames.name.name(),
				oldValue, getName());
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		String oldValue = getEmail();
		this.email = email;
		pcs.firePropertyChange(PropertyNames.email.name(),
				oldValue, getEmail());
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		String oldValue = getMobilePhone();
		this.mobilePhone = mobilePhone;
		pcs.firePropertyChange(PropertyNames.mobilePhone
				.name(), oldValue, getMobilePhone());
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		String oldValue = getPhone();
		this.phone = phone;
		pcs.firePropertyChange(PropertyNames.phone.name(),
				oldValue, getPhone());
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		String oldValue = getStatusCode();
		this.statusCode = statusCode;
		pcs.firePropertyChange(PropertyNames.statusCode
				.name(), oldValue, getStatusCode());
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		boolean oldValue = isActive();
		this.isActive = isActive;
		pcs.firePropertyChange(PropertyNames.active.name(),
				oldValue, isActive());
	}

	public PreferedAlarmType getPreferedAlarmType() {
		return preferedAlarmType;
	}

	public void setPreferedAlarmType(PreferedAlarmType preferedAlarmType) {
		PreferedAlarmType oldValue = getPreferedAlarmType();
		this.preferedAlarmType = preferedAlarmType;
		pcs.firePropertyChange(
				PropertyNames.preferedAlarmType.name(), oldValue,
				getPreferedAlarmType());
	}

	// public void copyStateOf(AlarmbearbeiterBean otherBean) {
	// throw new UnsupportedOperationException("not implemented yet.");
	// }

	@Override
	public AlarmbearbeiterBean getClone() {
		AlarmbearbeiterBean bean = new AlarmbearbeiterBean();
		bean.setUserID(this.getUserID());
		bean.setActive(this.isActive);
		bean.setConfirmCode(this.getConfirmCode());
		bean.setEmail(this.getEmail());
		bean.setMobilePhone(this.getMobilePhone());
		bean.setName(this.getName());
		bean.setPhone(this.getPhone());
		bean.setPreferedAlarmType(this.getPreferedAlarmType());
		bean.setStatusCode(this.getStatusCode());
		return bean;
	}

	public String getDisplayName() {
		return getName() != null ? getName() : "(ohne Name)";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((confirmCode == null) ? 0 : confirmCode.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result
				+ ((mobilePhone == null) ? 0 : mobilePhone.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime
				* result
				+ ((preferedAlarmType == null) ? 0 : preferedAlarmType
						.hashCode());
		result = prime * result
				+ ((statusCode == null) ? 0 : statusCode.hashCode());
		result = prime * result + userID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AlarmbearbeiterBean other = (AlarmbearbeiterBean) obj;
		if (confirmCode == null) {
			if (other.confirmCode != null)
				return false;
		} else if (!confirmCode.equals(other.confirmCode))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (isActive != other.isActive)
			return false;
		if (mobilePhone == null) {
			if (other.mobilePhone != null)
				return false;
		} else if (!mobilePhone.equals(other.mobilePhone))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (preferedAlarmType == null) {
			if (other.preferedAlarmType != null)
				return false;
		} else if (!preferedAlarmType.equals(other.preferedAlarmType))
			return false;
		if (statusCode == null) {
			if (other.statusCode != null)
				return false;
		} else if (!statusCode.equals(other.statusCode))
			return false;
		if (userID != other.userID)
			return false;
		return true;
	}

	@Override
	public void updateState(AlarmbearbeiterBean bean) {
		this.setUserID(bean.getUserID());
		this.setActive(bean.isActive);
		this.setConfirmCode(bean.getConfirmCode());
		this.setEmail(bean.getEmail());
		this.setMobilePhone(bean.getMobilePhone());
		this.setName(bean.getName());
		this.setPhone(bean.getPhone());
		this.setPreferedAlarmType(bean.getPreferedAlarmType());
		this.setStatusCode(this.getStatusCode());

	}

	@Override
	public int getID() {
		return this.getUserID();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
