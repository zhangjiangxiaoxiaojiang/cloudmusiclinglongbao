package com.jinxin.cloudmusic.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.constant.LocalConstant;
import com.jinxin.cloudmusic.util.L;
import com.jinxin.cloudmusic.util.ToastUtil;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Created by XTER on 2017/2/14.
 * 数据通信
 */
public class UpdateDataHandler extends IoHandlerAdapter {
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String content = (String) message;
		JSONObject jsonObject = JSON.parseObject(content);

//		L.d(content);
		updateData(jsonObject);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		L.d("进入空置状态");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		L.w(cause.getMessage());
	}

	public void updateData(JSONObject jo) {
		if (jo.containsKey("rspCode")) {
			String rspCode = jo.getString("rspCode");
			if (!rspCode.equals("0000")) {
				L.i(jo.toString());
				ToastUtil.showLong(JxscApp.getContext(), "数据异常，请重新连接");
				//EventBus.getDefault().post(new UpdateFailedEvent());
				return;
			}
		}

		if (jo.containsKey("area") && jo.containsKey("content")) {
			String area = jo.getString("area");
			String content = jo.getString("content");

			switch (area) {
				case LocalConstant.AREA_ALARM:
					break;
				case LocalConstant.AREA_STATE:
					break;
				case LocalConstant.AREA_ALL:
				default:
					boolean isFinished = saveData(content);
					/*if (isFinished)
						EventBus.getDefault().post(new UpdateFinishEvent());
					else
						EventBus.getDefault().post(new UpdateFailedEvent());*/
					break;
			}
		}

	}

	/**
	 * 更新存储数据
	 * {
	 * "rspCode": "0000",
	 * "area": "all",
	 * "time": "20170214174713",
	 * "content": [
	 * {
	 * "tableName": "could_setting",
	 * "tableContent": [... ]
	 * }
	 * ]
	 *
	 * @param jsonStr json字段
	 */
	public boolean saveData(String jsonStr) {
		JSONArray jsonArray = JSON.parseArray(jsonStr);

		for (int i = 0; i < jsonArray.size(); i++) {

			JSONObject jsonObject1 = jsonArray.getJSONObject(i);

			if (jsonObject1 != null && jsonObject1.containsKey("tableName") && jsonObject1.containsKey("tableContent")) {

				String tableName = jsonObject1.getString("tableName");
				String tableContent = jsonObject1.getString("tableContent");


				L.d(tableName);
				L.d(tableContent);

				/*switch (tableName) {
					case LocalConstant.CLOUD_SETTING:
						DBM.getCurrentOrm().deleteAll(CloudSetting.class);
						List<CloudSetting> cloudSettingList = JSON.parseArray(tableContent, CloudSetting.class);
						DBM.getCurrentOrm().save(cloudSettingList);
						L.i(tableName + " size " + cloudSettingList.size());
						break;
					case LocalConstant.CUSTOMER:
						DBM.getCurrentOrm().deleteAll(Customer.class);
						List<Customer> customerList = JSON.parseArray(tableContent, Customer.class);
						DBM.getCurrentOrm().save(customerList);
						if (customerList.size() > 0)
							SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_ACCOUNT, customerList.get(0).getCreateUser());
						L.i(tableName + " size " + customerList.size());
						break;
					case LocalConstant.CUSTOMER_AREA:
						DBM.getCurrentOrm().deleteAll(CustomerArea.class);
						List<CustomerArea> customerAreaList = JSON.parseArray(tableContent, CustomerArea.class);
						DBM.getCurrentOrm().save(customerAreaList);
						L.i(tableName + " size " + customerAreaList.size());
						break;
					case LocalConstant.CUSTOMER_PATTERN:
						DBM.getCurrentOrm().deleteAll(CustomerPattern.class);
						List<CustomerPattern> customerPatternList = JSON.parseArray(tableContent, CustomerPattern.class);
						DBM.getCurrentOrm().save(customerPatternList);
						L.i(tableName + " size " + customerPatternList.size());
						break;
					case LocalConstant.CUSTOMER_PRODUCT:
						DBM.getCurrentOrm().deleteAll(CustomerProduct.class);
						List<CustomerProduct> customerProductList = JSON.parseArray(tableContent, CustomerProduct.class);
						DBM.getCurrentOrm().save(customerProductList);
						L.i(tableName + " size " + customerProductList.size());
						if (customerProductList.size() < 1)
							return false;
						break;
					case LocalConstant.FUN_DETAIL:
						DBM.getCurrentOrm().deleteAll(FunDetail.class);
						List<FunDetail> funDetailList = JSON.parseArray(tableContent, FunDetail.class);
						DBM.getCurrentOrm().save(funDetailList);
						L.i(tableName + " size " + funDetailList.size());
						if (funDetailList.size() < 1)
							return false;
						break;
					case LocalConstant.FUN_DETAIL_CONFIG:
						DBM.getCurrentOrm().deleteAll(FunDetailConfig.class);
						List<FunDetailConfig> funDetailConfigList = JSON.parseArray(tableContent, FunDetailConfig.class);
						DBM.getCurrentOrm().save(funDetailConfigList);
						L.i(tableName + " size " + funDetailConfigList.size());
						break;
					case LocalConstant.MUSIC:
						DBM.getCurrentOrm().deleteAll(Music.class);
						List<Music> musicList = JSON.parseArray(tableContent, Music.class);
						DBM.getCurrentOrm().save(musicList);
						L.i(tableName + " size " + musicList.size());
						break;
					case LocalConstant.PRODUCT_DOOR_CONTACT:
						DBM.getCurrentOrm().deleteAll(ProductDoorContact.class);
						List<ProductDoorContact> productDoorContactList = JSON.parseArray(tableContent, ProductDoorContact.class);
						DBM.getCurrentOrm().save(productDoorContactList);
						L.i(tableName + " size " + productDoorContactList.size());
						break;
					case LocalConstant.PRODUCT_FUN:
						DBM.getCurrentOrm().deleteAll(ProductFun.class);
						List<ProductFun> productFunList = JSON.parseArray(tableContent, ProductFun.class);
						DBM.getCurrentOrm().save(productFunList);
						L.i(tableName + " size " + productFunList.size());
						if (productFunList.size() < 1)
							return false;
						break;
					case LocalConstant.PRODUCT_PATTERN_OPERATION:
						DBM.getCurrentOrm().deleteAll(ProductPatternOperation.class);
						List<ProductPatternOperation> productPatternOperationList = JSON.parseArray(tableContent, ProductPatternOperation.class);
						DBM.getCurrentOrm().save(productPatternOperationList);
						L.i(tableName + " size " + productPatternOperationList.size());
						break;
					case LocalConstant.PRODUCT_REGISTER:
						DBM.getCurrentOrm().deleteAll(ProductRegister.class);
						List<ProductRegister> productRegisterList = JSON.parseArray(tableContent, ProductRegister.class);
						DBM.getCurrentOrm().save(productRegisterList);
						L.i(tableName + " size " + productRegisterList.size());
						//此表数据为空，则更新数据失败
						if (productRegisterList.size() < 1)
							return false;
						break;
					case LocalConstant.USER:
						DBM.getCurrentOrm().deleteAll(User.class);
						List<User> userList = JSON.parseArray(tableContent, User.class);
						for (User user : userList) {
							SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_ACCOUNT, user.getAccount());
							SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_SUB_ACCOUNT, user.getSubAccunt());
							SPM.saveStr(SPM.CONSTANT, LocalConstant.KEY_PASSWORD, user.getSecretKey());
						}
						DBM.getCurrentOrm().save(userList);
						L.i(tableName + " size " + userList.size());
						break;
				}*/
			}
		}
		return true;
	}
}
