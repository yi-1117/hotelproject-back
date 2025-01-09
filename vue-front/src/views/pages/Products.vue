<template>
    <h3>產品功能</h3>

    <div class="row">
        <div class="col-2">
            <button type="button" class="btn btn-primary" @click="openModal('insert')">開啟新增</button>

        </div>
        <div class="col-4">
            <input type="text" placeholder="請輸入產品名稱" v-model="findName" @input="callFind(1)">
        </div>
        <div class="col-6">
            <ProductRows :choices="[2, 3, 4, 5, 7, 20]" :total="total"
                        v-model="rows" @change="callFind"></ProductRows>
        </div>
    </div>
    <br>

    <div class="row">
        <Paginate v-show="total>0" :first-last-button="true"
            first-button-text="&lt;&lt;" last-button-text="&gt;&gt;"
            prev-text="&lt;" next-text="&gt;"
            :page-count="pages" :initial-page="current" v-model="current"
            :click-handler="callFind">
        </Paginate>
    </div>
    <br>

    <div class="row">
        <ProductCard v-for="product in products" :key="product.id"
                    :item="product" @delete="callRemove" @open-update="openModal">
        </ProductCard>
    </div>

    <ProductModal ref="productModal" v-model:product="product"
                    :is-show-button-insert="isShowButtonInsert"
                    @insert="callCreate" @update="callModify">
    </ProductModal>
</template>
    
<script setup>
    import axiosapi from '@/plugins/axios';
    import Swal from 'sweetalert2';
    import { ref, onMounted } from 'vue';
    import ProductCard from '@/components/ProductCard.vue';
    import ProductRows from '@/components/ProductRows.vue';
    import ProductModal from '@/components/ProductModal.vue';
    import Paginate from 'vuejs-paginate-next'

    const findName = ref("");
    const products = ref([]);
    const product = ref({});
    const productModal = ref(null);
    const isShowButtonInsert = ref(false);

    //分頁 start
    const total = ref(0);       //總筆數
    const rows = ref(4);       //每頁筆數
    const pages = ref(0);      //總頁數
    const current = ref(1);     //目前頁碼
    const start = ref(0);       //從哪筆資料開始抓
    const lastPageRows = ref(0);
    //分頁 end

    async function callCreate() {
        if(product.value.id==="") {
            product.value.id = null;
        }
        if(product.value.name==="") {
            product.value.name = null;
        }
        if(product.value.price==="") {
            product.value.price = null;
        }
        if(product.value.make==="") {
            product.value.make = null;
        }
        if(product.value.expire==="") {
            product.value.expire = null;
        }

        const body = product.value;
        try {
            const response = await axiosapi.post("/ajax/pages/products", body);
            console.log("response", response);
            if (response.data.success) {
                Swal.fire({
                    title: response.data.message,
                    icon: "success"
                }).then(function(result) {
                    productModal.value.hideModal();
                    callFind(current.value);

                });
            } else {
                Swal.fire({
                    title: response.data.message,
                    icon: "warning"
                });
            }
        } catch (error) {
            console.log("error", error);
            Swal.fire({
                title: "執行失敗:" + error.message,
                icon: "error"
            });
        }
    }

    function callRemove(id) {
        Swal.fire({
            title: "確定刪除？",
            icon: "question",
            showCancelButton: true
        }).then(async function (result) {
            if (result.isConfirmed) {
                try {
                    const response = await axiosapi.delete("/ajax/pages/products/"+id);
                    console.log("response", response);
                    if (response.data.success) {
                        Swal.fire({
                            title: response.data.message,
                            icon: "success"
                        }).then(function(result) {
                            if(lastPageRows.value == 1 && current.value > 1) {
                                current.value = current.value - 1;
                            }
                            callFind(current.value);
                        });
                    } else {
                        Swal.fire({
                            title: response.data.message,
                            icon: "warning"
                        });
                    }
                } catch (error) {
                    console.log("error", error);
                    Swal.fire({
                        title: "執行失敗:" + error.message,
                        icon: "error"
                    });
                }
            }
        });
    }
    function callFindById(id) {
        axiosapi.get("/ajax/pages/products/"+id).then(function (response) {
            console.log("response", response);
            product.value = response.data.list[0];

        }).catch(function (error) {
            console.log("error", error);

        });
    }
    async function callModify() {
        if(product.value.id==="") {
            product.value.id = null;
        }
        if(product.value.name==="") {
            product.value.name = null;
        }
        if(product.value.price==="") {
            product.value.price = null;
        }
        if(product.value.make==="") {
            product.value.make = null;
        }
        if(product.value.expire==="") {
            product.value.expire = null;
        }

        const body = product.value;
        try {
            const response = await axiosapi.put("/ajax/pages/products/"+product.value.id, body);
            console.log("response", response);
            if (response.data.success) {
                Swal.fire({
                    title: response.data.message,
                    icon: "success"
                }).then(function(result) {
                    productModal.value.hideModal();
                    callFind(current.value);
                });
            } else {
                Swal.fire({
                    title: response.data.message,
                    icon: "warning"
                });
            }
        } catch (error) {
            console.log("error", error);
            Swal.fire({
                title: "執行失敗:" + error.message,
                icon: "error"
            });
        }
    }
    function openModal(action, id) {
        console.log("openModal", action, id);
        if(action==="insert") {
            isShowButtonInsert.value = true;
            product.value = { };
        } else {
            isShowButtonInsert.value = false;
            callFindById(id);
        }

        productModal.value.showModal();
    }
    onMounted(function() {
        callFind();
    });

    function callFind(page) {
        if(page) {
            current.value = page;
            start.value = (page - 1) * rows.value;
        } else {
            current.value = 1;
            start.value = 0;
        }

        if(findName.value==="") {
            findName.value = null;
        }

        const body = {
            "start": start.value,
            "rows": rows.value,
            "dir": false,
            "sort": "id",
            "name": findName.value
        };
        axiosapi.post("/ajax/pages/products/find", body).then(function (response) {
            console.log("response", response);
            products.value = response.data.list;

            //分頁 start
            total.value = response.data.count;
            pages.value = Math.ceil(response.data.count / rows.value);
            lastPageRows.value = total.value % rows.value;
            //分頁 end

        }).catch(function (error) {
            console.log("error", error);
            Swal.fire({
                title: "執行失敗:" + error.message,
                icon: "error"
            });
        });
    }
</script>

<style>

</style>