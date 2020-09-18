package com.example.beerrunshipper.Callback;

import com.example.beerrunshipper.Model.ShippingOrderModel;

import java.util.List;

public interface IShippingOrderCallBackListener {
    void onShippingOrderLoadSuccess(List<ShippingOrderModel> shippingOrderModelList);
    void onShippingOrderLoadFailed(String message);


}
