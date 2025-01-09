<template>
    <div ref="exampleModalRef" class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">Modal title</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <table>
                        <tbody>
                            <tr>
                                <td>Id : </td>
                                <td><input type="text" name="id" :value="product.id"
                                            @input="doInput('id', $event.target.value)"></td>
                            </tr>
                            <tr>
                                <td>Name : </td>
                                <td><input type="text" name="name" :value="product.name"
                                            @input="doInput('name', $event.target.value)"></td>
                            </tr>
                            <tr>
                                <td>Price : </td>
                                <td><input type="text" name="price" :value="product.price"
                                            @input="doInput('price', $event.target.value)"></td>
                            </tr>
                            <tr>
                                <td>Make : </td>
                                <td><input type="text" name="make" :value="product.make"
                                            @input="doInput('make', $event.target.value)"></td>
                            </tr>
                            <tr>
                                <td>Expire : </td>
                                <td><input type="text" name="expire" :value="product.expire"
                                            @input="doInput('expire', $event.target.value)"></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary"
                            @click="emits('insert')" v-show="isShowButtonInsert">新增</button>
                    <button type="button" class="btn btn-primary"
                            @click="emits('update')" v-show="!isShowButtonInsert">修改</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

</template>
    
<script setup>
    const props = defineProps(["product", "isShowButtonInsert"]);
    const emits = defineEmits(["update:product", "insert", "update"]);
    function doInput(key, value) {
        console.log("doInput", key, value);
        const obj = {
            ...props.product,
            [key]: value
        };
        emits("update:product", obj);
    }




    import { ref, onMounted } from "vue";
    import bootstrap from 'bootstrap/dist/js/bootstrap.bundle.min.js'

    const exampleModalRef = ref(null);
    const exampleModalObj = ref(null);
    onMounted(function() {
        exampleModalObj.value = new bootstrap.Modal(exampleModalRef.value);
    });
    function showModal() {
        exampleModalObj.value.show();
    }
    function hideModal() {
        exampleModalObj.value.hide();
    }
    defineExpose({
        showModal, hideModal
    });
</script>
    
<style>
    
</style>