let controladorGnrl;
let ctrlGeneral;
function cargarSucursales(){
    fetch('modulos/moduloSucursal/vistaSucursal.html')
            .then(response=>response.text())
            .then(html=>{
                document.getElementById("main").innerHTML=html;
                import("../modulos/moduloSucursal/controladorSucursal.js").then(
                        function (controller) {
                            controladorGnrl=controller;
                        });
    });
}
