package com.hzlz.aviation.feature.community.model;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;


/**
 * @author huangwei
 * date : 2021/7/30
 * desc : 放心爱
 **/
public class FXAModel {

    //活动举办地点
    private String address;
    //费用
    private int cost;
    //创建时间
    private String createDate;
    //创建用户id
    private int createUserId;
    //押金
    private int deposit;
    //活动结束时间
    private String endDate;
    //活动关联圈子
    private int groupId;
    //活动id
    private int id;
    //活动报名结束时间
    private String joinEndDate;
    //活动报名开始时间
    private String joinStartDate;
    //参数活动成员信息
    private MemberBean member;
    //活动名称
    private String name;
    //人数
    private int peopleCount;
    //活动开始时间
    private String startDate;
    //活动状态
    private int status;
    //活动类型，默认=0，保留字段
    private int type;
    //更新时间
    private String updateDate;
    //更新用户id
    private int updateUserId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJoinEndDate() {
        return joinEndDate;
    }

    public void setJoinEndDate(String joinEndDate) {
        this.joinEndDate = joinEndDate;
    }

    public String getJoinStartDate() {
        return joinStartDate;
    }

    public void setJoinStartDate(String joinStartDate) {
        this.joinStartDate = joinStartDate;
    }

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(int updateUserId) {
        this.updateUserId = updateUserId;
    }

    public static class MemberBean {
        //活动id
        private int activityId;
        //年龄
        private String age;
        //审核状态,0-待审核，1-审核通过，2-审核拒绝
        private int auditStatus;
        //毕业院校
        private String chooseCondition;
        //活动序列号
        private String code;
        //工作单位
        private String company;
        //工作单位性质：1-省团委、2-工团委
        private int companyType;
        //学历
        private String education;
        //生育状态：1-已育、2-未育
        private int fertilityStatus;
        //1-男、2-女
        private int gender;
        //毕业院校
        private String graduateSchool;
        //身高,cm
        private int height;
        //兴趣爱好
        private String hobby;
        // 房、车购置情况
        private String houseAndCar;
        //活动成员表id
        private String id;
        //专业
        private String major;
        //婚姻状态：1-已婚、2-未婚、3-离异、4-丧偶
        private int maritalStatus;
        //手机号码
        private String mobile;
        //月收入，元
        private int monthlyIncome;
        //籍贯
        private String nativePlace;
        //配对信息
        private PairInfoBean pairInfo;
        //真实姓名
        private String realName;
        //签到信息
        private SignInfoBean signInfo;
        //微信
        private String wechatNo;
        //当前活动
        private ActivityPart currentPart;

        public ActivityPart getCurrentPart() {
            return currentPart;
        }

        public void setCurrentPart(ActivityPart currentPart) {
            this.currentPart = currentPart;
        }

        public int getActivityId() {
            return activityId;
        }

        public void setActivityId(int activityId) {
            this.activityId = activityId;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public int getAuditStatus() {
            return auditStatus;
        }

        public void setAuditStatus(int auditStatus) {
            this.auditStatus = auditStatus;
        }

        public String getChooseCondition() {
            return chooseCondition;
        }

        public void setChooseCondition(String chooseCondition) {
            this.chooseCondition = chooseCondition;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public int getCompanyType() {
            return companyType;
        }

        public void setCompanyType(int companyType) {
            this.companyType = companyType;
        }

        public String getEducation() {
            return education;
        }

        public void setEducation(String education) {
            this.education = education;
        }

        public int getFertilityStatus() {
            return fertilityStatus;
        }

        public void setFertilityStatus(int fertilityStatus) {
            this.fertilityStatus = fertilityStatus;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getGraduateSchool() {
            return graduateSchool;
        }

        public void setGraduateSchool(String graduateSchool) {
            this.graduateSchool = graduateSchool;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getHobby() {
            return hobby;
        }

        public void setHobby(String hobby) {
            this.hobby = hobby;
        }

        public String getHouseAndCar() {
            return houseAndCar;
        }

        public void setHouseAndCar(String houseAndCar) {
            this.houseAndCar = houseAndCar;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMajor() {
            return major;
        }

        public void setMajor(String major) {
            this.major = major;
        }

        public int getMaritalStatus() {
            return maritalStatus;
        }

        public void setMaritalStatus(int maritalStatus) {
            this.maritalStatus = maritalStatus;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getMonthlyIncome() {
            return monthlyIncome;
        }

        public void setMonthlyIncome(int monthlyIncome) {
            this.monthlyIncome = monthlyIncome;
        }

        public String getNativePlace() {
            return nativePlace;
        }

        public void setNativePlace(String nativePlace) {
            this.nativePlace = nativePlace;
        }

        public MemberBean.PairInfoBean getPairInfo() {
            return pairInfo;
        }

        public void setPairInfo(MemberBean.PairInfoBean pairInfo) {
            this.pairInfo = pairInfo;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public MemberBean.SignInfoBean getSignInfo() {
            return signInfo;
        }

        public void setSignInfo(MemberBean.SignInfoBean signInfo) {
            this.signInfo = signInfo;
        }

        public String getWechatNo() {
            return wechatNo;
        }

        public void setWechatNo(String wechatNo) {
            this.wechatNo = wechatNo;
        }

        public static class PairInfoBean {
            //配对id
            private int id;
            //当前成员号码牌
            private int memberCode;
            //当前成员id
            private int memberId;
            //是否，配对成功
            private boolean paired;
            //选择成员号码牌
            private String selectMemberCode;
            //选择成员id
            private int selectMemberId;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getMemberCode() {
                return memberCode;
            }

            public void setMemberCode(int memberCode) {
                this.memberCode = memberCode;
            }

            public int getMemberId() {
                return memberId;
            }

            public void setMemberId(int memberId) {
                this.memberId = memberId;
            }

            public boolean isPaired() {
                return paired;
            }

            public void setPaired(boolean paired) {
                this.paired = paired;
            }

            public String getSelectMemberCode() {
                return selectMemberCode;
            }

            public void setSelectMemberCode(String selectMemberCode) {
                this.selectMemberCode = selectMemberCode;
            }

            public int getSelectMemberId() {
                return selectMemberId;
            }

            public void setSelectMemberId(int selectMemberId) {
                this.selectMemberId = selectMemberId;
            }
        }

        public static class SignInfoBean {
            //签到id
            private int id;
            //签到成员id
            private int memberId;
            //签到时间
            private String signDate;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getMemberId() {
                return memberId;
            }

            public void setMemberId(int memberId) {
                this.memberId = memberId;
            }

            public String getSignDate() {
                return signDate;
            }

            public void setSignDate(String signDate) {
                this.signDate = signDate;
            }
        }
    }

    public static class ActivityPart {
        private String name;
        private @FXAType
        int partType;
        private int status;
        private Object info;

        public void setInfo(Object info) {
            this.info = info;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPartType() {
            return partType;
        }

        public void setPartType(int partType) {
            this.partType = partType;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public boolean isCanClick() {
        if (member.currentPart.partType == FXAType.SIGN) {
            return (member.currentPart.status == 1 && member.signInfo == null);
        } else if(member.currentPart.partType == FXAType.PAIR){
            return (member.currentPart.status == 1 && member.pairInfo == null);
        }
        return false;
    }

    public String statusString() {
        int res = 0;
        if (member.currentPart.partType == FXAType.SIGN) {
            res = (member.currentPart.status == 1 && member.signInfo == null) || member.currentPart.status == 0 ? R.string.sign : R.string.sign_finish;
        } else if (member.currentPart.partType == FXAType.PAIR) {
            res = (member.currentPart.status == 1 && member.pairInfo == null) || member.currentPart.status == 0 ? R.string.pair : R.string.pair_finish;
        }
        return GVideoRuntime.getAppContext().getString(res);
    }

    public String getGenderString() {
        if (member != null) {
            if (member.gender == 1) {
                return "男";
            } else {
                return "女";
            }
        }
        return "";
    }

    public Drawable getGenderDrawable() {
        return getGenderDrawable(member.gender);
    }

    public Drawable getGenderOppositeDrawable() {
        return getGenderDrawable(member.gender == 1 ? 2 : 1);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Drawable getGenderDrawable(int gender) {
        int res = 0;
        if (gender == 1) {
            res = R.drawable.ic_fxa_male;
        } else {
            res = R.drawable.ic_fxa_female;
        }
        return GVideoRuntime.getAppContext().getResources().getDrawable(res);
    }

}
