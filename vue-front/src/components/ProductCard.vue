<template>
    <div class="col-lg-3 col-md-6">
        <div class="card">
            <img class="card-img-top" :src="`${path}${item.id}`" :alt="item.name">
            <div class="card-body">
                <h5 class="card-title">{{ item.name }}</h5>
                <div class="card-text text-danger text-end">NT$ {{item.price}}</div>
                <div class="row">
                    <div class="col text-start">
                        <a href="#" class="btn btn-primary" @click="doclick1(item.id)">開啟修改</a>
                    </div>
                    <div class="col text-end">
                        <a href="#" class="btn btn-primary" @click="doclick2(item.id)">刪除</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
    const path = import.meta.env.VITE_DETAIL_URL;
    const props = defineProps(["item"]);
    const emits = defineEmits(["openUpdate", "delete"]);
    function doclick1(id) {
        emits("openUpdate", "update", id);
    }
    function doclick2(id) {
        emits("delete", id);
    }
</script>
<style>

</style>
