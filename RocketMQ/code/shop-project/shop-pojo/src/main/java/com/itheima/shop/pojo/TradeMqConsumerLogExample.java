package com.itheima.shop.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TradeMqConsumerLogExample implements Serializable {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TradeMqConsumerLogExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andGroupNameIsNull() {
            addCriterion("group_name is null");
            return (Criteria) this;
        }

        public Criteria andGroupNameIsNotNull() {
            addCriterion("group_name is not null");
            return (Criteria) this;
        }

        public Criteria andGroupNameEqualTo(String value) {
            addCriterion("group_name =", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameNotEqualTo(String value) {
            addCriterion("group_name <>", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameGreaterThan(String value) {
            addCriterion("group_name >", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameGreaterThanOrEqualTo(String value) {
            addCriterion("group_name >=", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameLessThan(String value) {
            addCriterion("group_name <", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameLessThanOrEqualTo(String value) {
            addCriterion("group_name <=", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameLike(String value) {
            addCriterion("group_name like", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameNotLike(String value) {
            addCriterion("group_name not like", value, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameIn(List<String> values) {
            addCriterion("group_name in", values, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameNotIn(List<String> values) {
            addCriterion("group_name not in", values, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameBetween(String value1, String value2) {
            addCriterion("group_name between", value1, value2, "groupName");
            return (Criteria) this;
        }

        public Criteria andGroupNameNotBetween(String value1, String value2) {
            addCriterion("group_name not between", value1, value2, "groupName");
            return (Criteria) this;
        }

        public Criteria andMsgTagIsNull() {
            addCriterion("msg_tag is null");
            return (Criteria) this;
        }

        public Criteria andMsgTagIsNotNull() {
            addCriterion("msg_tag is not null");
            return (Criteria) this;
        }

        public Criteria andMsgTagEqualTo(String value) {
            addCriterion("msg_tag =", value, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagNotEqualTo(String value) {
            addCriterion("msg_tag <>", value, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagGreaterThan(String value) {
            addCriterion("msg_tag >", value, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagGreaterThanOrEqualTo(String value) {
            addCriterion("msg_tag >=", value, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagLessThan(String value) {
            addCriterion("msg_tag <", value, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagLessThanOrEqualTo(String value) {
            addCriterion("msg_tag <=", value, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagLike(String value) {
            addCriterion("msg_tag like", value, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagNotLike(String value) {
            addCriterion("msg_tag not like", value, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagIn(List<String> values) {
            addCriterion("msg_tag in", values, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagNotIn(List<String> values) {
            addCriterion("msg_tag not in", values, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagBetween(String value1, String value2) {
            addCriterion("msg_tag between", value1, value2, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgTagNotBetween(String value1, String value2) {
            addCriterion("msg_tag not between", value1, value2, "msgTag");
            return (Criteria) this;
        }

        public Criteria andMsgKeyIsNull() {
            addCriterion("msg_key is null");
            return (Criteria) this;
        }

        public Criteria andMsgKeyIsNotNull() {
            addCriterion("msg_key is not null");
            return (Criteria) this;
        }

        public Criteria andMsgKeyEqualTo(String value) {
            addCriterion("msg_key =", value, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyNotEqualTo(String value) {
            addCriterion("msg_key <>", value, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyGreaterThan(String value) {
            addCriterion("msg_key >", value, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyGreaterThanOrEqualTo(String value) {
            addCriterion("msg_key >=", value, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyLessThan(String value) {
            addCriterion("msg_key <", value, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyLessThanOrEqualTo(String value) {
            addCriterion("msg_key <=", value, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyLike(String value) {
            addCriterion("msg_key like", value, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyNotLike(String value) {
            addCriterion("msg_key not like", value, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyIn(List<String> values) {
            addCriterion("msg_key in", values, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyNotIn(List<String> values) {
            addCriterion("msg_key not in", values, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyBetween(String value1, String value2) {
            addCriterion("msg_key between", value1, value2, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgKeyNotBetween(String value1, String value2) {
            addCriterion("msg_key not between", value1, value2, "msgKey");
            return (Criteria) this;
        }

        public Criteria andMsgIdIsNull() {
            addCriterion("msg_id is null");
            return (Criteria) this;
        }

        public Criteria andMsgIdIsNotNull() {
            addCriterion("msg_id is not null");
            return (Criteria) this;
        }

        public Criteria andMsgIdEqualTo(String value) {
            addCriterion("msg_id =", value, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdNotEqualTo(String value) {
            addCriterion("msg_id <>", value, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdGreaterThan(String value) {
            addCriterion("msg_id >", value, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdGreaterThanOrEqualTo(String value) {
            addCriterion("msg_id >=", value, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdLessThan(String value) {
            addCriterion("msg_id <", value, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdLessThanOrEqualTo(String value) {
            addCriterion("msg_id <=", value, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdLike(String value) {
            addCriterion("msg_id like", value, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdNotLike(String value) {
            addCriterion("msg_id not like", value, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdIn(List<String> values) {
            addCriterion("msg_id in", values, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdNotIn(List<String> values) {
            addCriterion("msg_id not in", values, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdBetween(String value1, String value2) {
            addCriterion("msg_id between", value1, value2, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgIdNotBetween(String value1, String value2) {
            addCriterion("msg_id not between", value1, value2, "msgId");
            return (Criteria) this;
        }

        public Criteria andMsgBodyIsNull() {
            addCriterion("msg_body is null");
            return (Criteria) this;
        }

        public Criteria andMsgBodyIsNotNull() {
            addCriterion("msg_body is not null");
            return (Criteria) this;
        }

        public Criteria andMsgBodyEqualTo(String value) {
            addCriterion("msg_body =", value, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyNotEqualTo(String value) {
            addCriterion("msg_body <>", value, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyGreaterThan(String value) {
            addCriterion("msg_body >", value, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyGreaterThanOrEqualTo(String value) {
            addCriterion("msg_body >=", value, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyLessThan(String value) {
            addCriterion("msg_body <", value, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyLessThanOrEqualTo(String value) {
            addCriterion("msg_body <=", value, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyLike(String value) {
            addCriterion("msg_body like", value, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyNotLike(String value) {
            addCriterion("msg_body not like", value, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyIn(List<String> values) {
            addCriterion("msg_body in", values, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyNotIn(List<String> values) {
            addCriterion("msg_body not in", values, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyBetween(String value1, String value2) {
            addCriterion("msg_body between", value1, value2, "msgBody");
            return (Criteria) this;
        }

        public Criteria andMsgBodyNotBetween(String value1, String value2) {
            addCriterion("msg_body not between", value1, value2, "msgBody");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusIsNull() {
            addCriterion("consumer_status is null");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusIsNotNull() {
            addCriterion("consumer_status is not null");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusEqualTo(Integer value) {
            addCriterion("consumer_status =", value, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusNotEqualTo(Integer value) {
            addCriterion("consumer_status <>", value, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusGreaterThan(Integer value) {
            addCriterion("consumer_status >", value, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("consumer_status >=", value, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusLessThan(Integer value) {
            addCriterion("consumer_status <", value, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusLessThanOrEqualTo(Integer value) {
            addCriterion("consumer_status <=", value, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusIn(List<Integer> values) {
            addCriterion("consumer_status in", values, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusNotIn(List<Integer> values) {
            addCriterion("consumer_status not in", values, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusBetween(Integer value1, Integer value2) {
            addCriterion("consumer_status between", value1, value2, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("consumer_status not between", value1, value2, "consumerStatus");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesIsNull() {
            addCriterion("consumer_times is null");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesIsNotNull() {
            addCriterion("consumer_times is not null");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesEqualTo(Integer value) {
            addCriterion("consumer_times =", value, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesNotEqualTo(Integer value) {
            addCriterion("consumer_times <>", value, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesGreaterThan(Integer value) {
            addCriterion("consumer_times >", value, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesGreaterThanOrEqualTo(Integer value) {
            addCriterion("consumer_times >=", value, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesLessThan(Integer value) {
            addCriterion("consumer_times <", value, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesLessThanOrEqualTo(Integer value) {
            addCriterion("consumer_times <=", value, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesIn(List<Integer> values) {
            addCriterion("consumer_times in", values, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesNotIn(List<Integer> values) {
            addCriterion("consumer_times not in", values, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesBetween(Integer value1, Integer value2) {
            addCriterion("consumer_times between", value1, value2, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimesNotBetween(Integer value1, Integer value2) {
            addCriterion("consumer_times not between", value1, value2, "consumerTimes");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampIsNull() {
            addCriterion("consumer_timestamp is null");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampIsNotNull() {
            addCriterion("consumer_timestamp is not null");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampEqualTo(Date value) {
            addCriterion("consumer_timestamp =", value, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampNotEqualTo(Date value) {
            addCriterion("consumer_timestamp <>", value, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampGreaterThan(Date value) {
            addCriterion("consumer_timestamp >", value, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampGreaterThanOrEqualTo(Date value) {
            addCriterion("consumer_timestamp >=", value, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampLessThan(Date value) {
            addCriterion("consumer_timestamp <", value, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampLessThanOrEqualTo(Date value) {
            addCriterion("consumer_timestamp <=", value, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampIn(List<Date> values) {
            addCriterion("consumer_timestamp in", values, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampNotIn(List<Date> values) {
            addCriterion("consumer_timestamp not in", values, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampBetween(Date value1, Date value2) {
            addCriterion("consumer_timestamp between", value1, value2, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andConsumerTimestampNotBetween(Date value1, Date value2) {
            addCriterion("consumer_timestamp not between", value1, value2, "consumerTimestamp");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNull() {
            addCriterion("remark is null");
            return (Criteria) this;
        }

        public Criteria andRemarkIsNotNull() {
            addCriterion("remark is not null");
            return (Criteria) this;
        }

        public Criteria andRemarkEqualTo(String value) {
            addCriterion("remark =", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotEqualTo(String value) {
            addCriterion("remark <>", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThan(String value) {
            addCriterion("remark >", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("remark >=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThan(String value) {
            addCriterion("remark <", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLessThanOrEqualTo(String value) {
            addCriterion("remark <=", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkLike(String value) {
            addCriterion("remark like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotLike(String value) {
            addCriterion("remark not like", value, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkIn(List<String> values) {
            addCriterion("remark in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotIn(List<String> values) {
            addCriterion("remark not in", values, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkBetween(String value1, String value2) {
            addCriterion("remark between", value1, value2, "remark");
            return (Criteria) this;
        }

        public Criteria andRemarkNotBetween(String value1, String value2) {
            addCriterion("remark not between", value1, value2, "remark");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}