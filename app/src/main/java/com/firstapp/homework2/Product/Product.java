package com.firstapp.homework2.Product;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.firstapp.homework2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Product extends Fragment {

    private FirebaseFirestore db;
    private ArrayList<String> productDisplayList;
    private ArrayList<String> productIdsList;
    private ArrayAdapter<String> adapter;

    private ListView lvProducts;
    private TextInputEditText etProductName, etProductQuantity;
    private Button btnAddProduct, btnRemoveProduct;
    private TextView tvUsername;

    private int selectedPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        db = FirebaseFirestore.getInstance();

        tvUsername = view.findViewById(R.id.tvUsername);
        etProductName = view.findViewById(R.id.etProductName);
        etProductQuantity = view.findViewById(R.id.etProductQuantity);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        btnRemoveProduct = view.findViewById(R.id.btnRemoveProduct);
        lvProducts = view.findViewById(R.id.lvProducts);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail(); // Get user's email
            tvUsername.setText("משתמש: " + email);
        } else {
            tvUsername.setText("משתמש לא מחובר");
        }

        productDisplayList = new ArrayList<>();
        productIdsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, productDisplayList);
        lvProducts.setAdapter(adapter);
        lvProducts.setOnItemClickListener((parent, view1, position, id) -> {
            selectedPosition = position;
        });

        loadProductsFromFirestore();

        btnAddProduct.setOnClickListener(v -> addProduct());

        btnRemoveProduct.setOnClickListener(v -> removeProduct());

        return view;
    }

    private void loadProductsFromFirestore() {
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productDisplayList.clear();
                            productIdsList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productName = document.getString("name");
                                Long quantity = document.getLong("quantity");

                                if (productName != null && quantity != null) {
                                    productDisplayList.add(productName + " - כמות: " + quantity);
                                    productIdsList.add(document.getId());
                                }

                                Log.d("Firestore", document.getId() + " => " + document.getData());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                            Toast.makeText(getActivity(), "שגיאה בטעינת המוצרים", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void addProduct() {
        String productName = etProductName.getText().toString().trim();
        String quantityStr = etProductQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(getActivity(), "יש למלא את שם המוצר והכמות", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "כמות לא חוקית", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("quantity", quantity);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getActivity(), "המוצר נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                        productDisplayList.add(productName + " - כמות: " + quantity);
                        adapter.notifyDataSetChanged();
                        etProductName.setText("");
                        etProductQuantity.setText("");
                        loadProductsFromFirestore();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error adding document", e);
                        Toast.makeText(getActivity(), "שגיאה בהוספת המוצר", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeProduct() {
        if (selectedPosition == -1) {
            Toast.makeText(getActivity(), "נא לבחור מוצר להסרה", Toast.LENGTH_SHORT).show();
            return;
        }

        String productId = productIdsList.get(selectedPosition);

        db.collection("products").document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "המוצר נמחק בהצלחה!", Toast.LENGTH_SHORT).show();

                    productDisplayList.remove(selectedPosition);
                    productIdsList.remove(selectedPosition);
                    adapter.notifyDataSetChanged();

                    selectedPosition = -1;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "שגיאה במחיקת המוצר", Toast.LENGTH_SHORT).show();
                    Log.w("Firestore", "Error deleting document", e);
                });
    }


}
