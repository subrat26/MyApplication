package com.zaafoohpl.zaafoo.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.zaafoohpl.zaafoo.myapplication.R;
import com.zaafoohpl.zaafoo.myapplication.SessionManagement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.mateware.snacky.Snacky;
import io.paperdb.Paper;

public class CartFragment extends Fragment {


    Cart c;
    ArrayList<Menu> menuItems;
    ArrayList<Cart> mycart,cartList;
    ListView lv;
    CartAdapter adapter;
    int pos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.cart_fragment,container,false);
        getActivity().setTitle("Cart");
        lv=(ListView)rootView.findViewById(R.id.cart_list);
        Button payment=(Button)rootView.findViewById(R.id.payment);
        Paper.init(getActivity());
        mycart=new ArrayList<>();
        menuItems=new SessionManagement(getActivity()).loadCartItems();



        for(int i=0;i<menuItems.size();i++){
            c=new Cart();
            c.setName(menuItems.get(i).getName());
            c.setPrice(menuItems.get(i).getPrice());
            c.setAmount(menuItems.get(i).getAmount());
            c.setId(menuItems.get(i).getId());
            mycart.add(c);
        }

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mycart.isEmpty())
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.content_home_drawar,new PersonalRequest()).commit();
                else
                    Snacky.builder()
                            .setActivty(getActivity())
                            .setText("Oops..!! Cart Is Empty..")
                            .centerText()
                            .setDuration(Snacky.LENGTH_SHORT)
                            .error()
                            .show();
            }
        });


        if(mycart!=null) {

            adapter = new CartAdapter(getActivity(), R.layout.product_row_view, mycart);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setCancelable(false);
                    alert.setTitle("Remove Item");
                    alert.setMessage("Do You Want To Remove This Item ?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pos=position;
                            updateCartData();


                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                        }
                    });
                    AlertDialog dialog=alert.create();
                    dialog.show();

                }
            });

        }
        else
            Snacky.builder()
                    .setActivty(getActivity())
                    .setText("Oops..!! Cart Is Empty..")
                    .centerText()
                    .setDuration(Snacky.LENGTH_SHORT)
                    .error()
                    .show();
        return rootView;
    }



    private void updateCartData() {

        Cart item=(Cart)lv.getItemAtPosition(pos);
        removeMenuItem(item);
        mycart.remove(item);
        adapter.notifyDataSetChanged();

    }


    private void removeMenuItem(Cart item) {

        Iterator<Menu> it=menuItems.iterator();
        while(it.hasNext()){

            if(it.next().getId().equalsIgnoreCase(item.getId())){
                it.remove();
                Paper.book().write("cart_items",menuItems);
            }

        }
    }

}
