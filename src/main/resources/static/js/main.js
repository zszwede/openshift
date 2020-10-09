let dialogs = {}
let application = {}
let container = {}
let services = []
let routes = []

$(function () {
    $(".controlgroup").controlgroup()
    createDialogs()
    setupButtonHandlers()
});

function createDialogs(){
    let appDialog = $("#app-form").dialog({
        autoOpen: false,
        height: 200,
        width: 285,
        modal: true,
        buttons: {
            Cancel: function () {
                appDialog.dialog("close");
            },
            Save: function (){
                if(saveApplication()) {
                    appDialog.dialog("close");
                    $("#create-container").show()
                }
            }
        },
        close: function () {
        }
    })
    dialogs["application"] = appDialog


    let containerDialog = $("#container-form").dialog({
        autoOpen: false,
        height: 620,
        width: 445,
        modal: true,
        buttons: {
            Cancel: function () {
                containerDialog.dialog("close");
            },
            Save: function (){
                if(saveContainer()) {
                    containerDialog.dialog("close");
                    $("#create-services").show()
                }
            }
        },
        close: function () {
        }
    })
    dialogs["container"] = containerDialog

    let servicesDialog = $("#service-form").dialog({
        autoOpen: false,
        height: 520,
        width: 750,
        modal: true,
        buttons: {
            Cancel: function () {
                servicesDialog.dialog("close");
            },
            Save: function (){
                if(saveServices()) {
                    servicesDialog.dialog("close");
                    $("#create-routes").show()
                }
            }
        },
        open:function (){
            populateContainerPorts()
        },
        close: function () {
        }
    })
    dialogs["services"] = servicesDialog

    let routesDialog = $("#routes-form").dialog({
        autoOpen: false,
        height: 310,
        width: 530,
        modal: true,
        buttons: {
            Cancel: function () {
                routesDialog.dialog("close");
            },
            Save: function (){
                if(saveRoutes()) {
                    routesDialog.dialog("close");
                    $("#generate").show()
                }
            }
        },
        open:function (){
            populateServices()
        },
        close: function () {
        }
    })
    dialogs["routes"] = routesDialog

}

function populateServices(){
/*    container["services"] = [
        {
            "name": "svc1",
            "ports": [
                {
                    "port" : 1234,
                    "targetPort" : 1234,
                    "name" : "pname2"
                },
                {
                    "port" : 4561,
                    "targetPort" : 4561,
                    "name" : "pname4"
                },
            ]
        },
        {
            "name": "svc2",
            "ports": [
                {
                    "port" : 1234,
                    "targetPort" : 1234,
                    "name" : "pname2"
                },
                {
                    "port" : 4561,
                    "targetPort" : 4561,
                    "name" : "pname4"
                },
            ]
        }
    ]*/
    services.forEach((o,i) => {
        let option = $("<option value='" + o["name"] + "'>" + o["name"] +  "</option>")
        $("#route-service-list").append(option)
    })
    $("#route-service-list").on('change', populateServicePortFieldsForRoute)
    populateServicePortFieldsForRoute()
}

function populateServicePortFieldsForRoute(){
    $("#route-service-ports-list").empty()
    let current = $("#route-service-list option:selected").val();
    let svc = services.filter(s => s["name"] === current)[0]
    svc["ports"].forEach((o,i) => {
        let option = $("<option value='" + o["port"] + "'>" + o["name"] + " (" + o["port"] +  ")</option>")
        $("#route-service-ports-list").append(option)
    })
}

function populateContainerPorts(){
/*    container["ports"] = [
        {
            "port":1234,
            "name" : "aaaa"
        },
        {
            "port":4321,
            "name" : "bbbb"
        },
        {
            "port":4321,
            "name" : "bbbb"
        },
        {
            "port":4321,
            "name" : "bbbb"
        },
        {
            "port":4321,
            "name" : "bbbb"
        },
        {
            "port":4321,
            "name" : "bbbb"
        }
    ]*/
    $("#svc-container-ports").html("")
    $("#svc-container-ports").on('change', populateServicePortFields)
    container["ports"].forEach((o,i) => {
        let option = $("<option port='" + o["port"] + "' port_name='" + o["name"] + "'  value='" + o["name"] + ":" + o["port"] + "'>" + o["name"] + ":" + o["port"] + "</option>")
        $("#svc-container-ports").append(option)
    })
    populateServicePortFields()
}

function populateServicePortFields(){
    let current = $("#svc-container-ports option:selected");
    let port = $(current).attr("port");
    let name = $(current).attr("port_name");
    $("#svc-port-name").val(name)
    $("#svc-port-src").val(port)
    $("#svc-port-dst").val(port)
}

function setupButtonHandlers() {
    $("#create-container").on("click", () => {
        dialogs["container"].dialog("open");
    })
    $("#create-app").on("click", () => {
        dialogs["application"].dialog("open");
    })

    $("#create-services").on("click", () => {
        dialogs["services"].dialog("open");
    })

    $("#create-routes").on("click", () => {
        dialogs["routes"].dialog("open");
    })

    $("#add-port-button").on("click", () => {
        let port = $("#add-port").val()
        let portName = $("#add-port-name").val()
        if (port === "" || portName === ""){
            return
        }
        if (port > 65535){
            return
        }
        let kv = port + ":" + portName
        let option = $("<option port='" + port + "' port_name='" + portName + "'  value='" + kv + "'>" + kv + "</option>")
        $("#add-port-list").append(option)
        $("#add-port").val("")
        $("#add-port-name").val("")
    })

    $("#remove-port-button").on("click", () => {
        $("#add-port-list option:selected").remove()
    })

    $("#add-env-button").on("click", () => {
        let key = $("#add-env-name").val()
        let val = $("#add-env-val").val()
        if(key === "" || val === ""){
            return
        }
        let kv = key + ":" + val
        let option = $("<option key='" + key + "' val='" + val + "' value='" + kv + "'>" + kv + "</option>")
        $("#add-env-list").append(option)
        $("#add-env-name").val("")
        $("#add-env-val").val("")
    })

    $("#remove-env-button").on("click", () => {
        $("#add-env-list option:selected").remove()
    })


    $("#add-mount-button").on("click", () => {
        let pvc = $("#add-mount-pvc").val()
        let dir = $("#add-mount-dir").val()
        let sub = $("#add-mount-subdir").val()

        if(pvc === "" || dir === "" || sub === ""){
            return
        }
        let kv = dir + ":" + sub
        let option = $("<option pvc='" + pvc + "' dir='" + dir + "' sub='" + sub + "' value='" + kv + "'>" + kv + "</option>")
        $("#add-mount-list").append(option)
        $("#add-mount-pvc").val("")
        $("#add-mount-dir").val("")
        $("#add-mount-subdir").val("")
    })

    $("#remove-mount-button").on("click", () => {
        $("#add-mount-list option:selected").remove()
    })

    $("#svc-add-port-button").on("click", () => {
        let name = $("#svc-port-name").val()
        let src = $("#svc-port-src").val()
        let dst = $("#svc-port-dst").val()

        if(name === "" || src === "" || dst === ""){
            return
        }
        let kv = name + " : " + src + " => " + dst
        let option = $("<option name='" + name + "' src='" + src + "' dst='" + dst + "' value='" + kv + "'>" + kv + "</option>")
        $("#svc-port-list").append(option)
        $("#svc-port-name").val("")
        $("#svc-port-src").val("")
        $("#svc-port-dst").val("")
        $("#svc-container-ports option:selected").remove()
        populateServicePortFields()
    })

    $("#route-add-button").on("click", () => {
        let name = $("#route-name").val()
        if(name === ""){
            return
        }
        let svc = $("#route-service-list option:selected").val()
        let port = $("#route-service-ports-list option:selected").val()
        let kv = name + " => " + svc + " => " + port
        let option = $("<option port='" + port + "' svc='" + svc + "' name='" + name + "'  >" + kv + "</option>")
        $("#routes-list").append(option)
    })

    $("#route-remove-button").on("click", () => {
        $("#routes-list option:selected").remove()
    })

    $("#svc-remove-port-button").on("click", () => {
        let selected = $("#svc-port-list option:selected")
        container["ports"].forEach((o,i) => {
            if ($(selected).attr("src") * 1 === o["port"]){
                let option = $("<option port='" + o["port"] + "' port_name='" + o["name"] + "'  value='" + o["name"] + ":" + o["port"] + "'>" + o["name"] + ":" + o["port"] + "</option>")
                $("#svc-container-ports").append(option)
                populateServicePortFields()
            }
        })
        selected.remove()
    })

    $("#svc-add-button").on("click", () => {
        let sname = $("#svc-name").val()
        if(sname === ""){
            return
        }
        let ports = []
        $("#svc-port-list option").each((i,o)=>{
            ports.unshift(getServicePortDiv($(o).attr("name"),$(o).attr("src"),$(o).attr("dst")))
        })
        let line = getServiceListLine()
        line.append(getServiceNameDiv(sname))
        line.append(wrapPorts(ports))
        $(line).find(".svc-delete").on('click',()=>{
            line.remove()
        })
        $("#svc-services-list").append(line)
        $("#svc-port-list").empty()
        $("#svc-name").val("")
        populateContainerPorts()
    })

    $("#generate").on('click', () => {
        application["container"] = container
        application["services"] = services
        application["routes"] = routes
        $.ajax({
            type: "POST",
            url: "/graph",
            dataType: "text",
            contentType: 'application/json',
            data: JSON.stringify(application),
            success: (data) =>{
                $("#output").val(data)
                $("#output").show()
            },
            error: function (xhr, ajaxOptions, thrownError) {
                alert(xhr.status);
                alert(thrownError);
            }
        })
    })


}

function getServiceNameDiv(name){
    return $("<div class='svc-name' name='" + name + "' style=\"float: left;height: 100%; width: 100px\">\n" +
        "<span>Service Name:</span>\n" +
        "<br>\n" +
        "<span><strong>&nbsp;&nbsp;" + name + "</strong></span>\n" +
        "<br/><input class='svc-delete' type='button' value='Delete'/> " +
        "</div>")
}

function getServicePortDiv(name, src,dst){
    return $("<div class='svc-def' name='" + name + "' src='" + src + "' dst='" + dst + "'  style=\"float: left;height: 100%; width: 25%\">\n" +
        "<span>Port Name:</span><span><strong>&nbsp;&nbsp;" + name + "</strong></span>\n" +
        "<br/>\n" +
        "<span>Source port:</span><span><strong>&nbsp;&nbsp;" + src + "</strong></span>\n" +
        "<br/>\n" +
        "<span>Target port:</span><span><strong>&nbsp;&nbsp;" + dst + "</strong></span>\n" +
        "</div>")
}

function wrapPorts(portsArray){
    let wrapper = $("<div class='svc-defs' style=\"overflow: auto; height: 50px\">")
    portsArray.forEach((p) => {
        wrapper.append(p)
    })
    return wrapper
}

function getServiceListLine(){
    return $("<div class='svc-line' style=\"width: 97%;padding: 5px; margin-top: 5px;margin-left: 5px;height: 50px;border-width: 1px;border-style: solid;border-color: dimgrey;\"></div>")
}


function saveRoutes(){
    routes = []
    $("#routes-list option").each((i,route) =>{
        let port = $(route).attr("port")
        let service = $(route).attr("svc")
        let name = $(route).attr("name")
        let r = {
            "name" : name,
            "routeDetails" : {
                "port":port,
                "svcName" : service
            }
        }
        routes.unshift(r)
    })
    return true
}

function saveServices(){
    services = []
    $("#svc-services-list .svc-line").each((i,svc) => {
        let name = $(svc).find(".svc-name").attr("name")
        let portArray = []
        $(svc).find(".svc-def").each((i,ports) =>{
            let pname = $(ports).attr("name")
            let src = $(ports).attr("src")
            let dst = $(ports).attr("dst")
            let port = {
                "port" : src,
                "targetPort" : dst,
                "name" : pname
            }
            portArray.unshift(port)
        })
        let service = {
            "name" : name,
            "ports" : portArray
        }
        services.unshift(service)
    })
    return true
}

function saveApplication() {
    let project = $("#app-name").val()
    let cluster = $("#app-cluster").val()
    if (cluster === "" || project === ""){
        return false
    }
    application["details"] = {
        "project": project,
        "cluster": cluster,
    };
    $("#app-name").val("")
    $("#app-cluster").val("")
    return true;
}

function saveContainer(){
    let appName = $("#container-name").val()
    let image = $("#image").val()
    let ports = []
    let envs = []
    let mounts = []
    let resources = {}
    $("#add-port-list option").each((i,o) =>{
        let port = {
            "port": $(o).attr("port"),
            "name": $(o).attr("port_name"),
        }
        ports.unshift(port)
    })
    $("#add-env-list option").each((i,o) =>{
        let env = {
            "key": $(o).attr("key"),
            "val": $(o).attr("val"),
        }
        envs.unshift(env)
    })
    $("#add-mount-list option").each((i,o) =>{
        let mount = {
            "pvc": $(o).attr("pvc"),
            "dir": $(o).attr("dir"),
            "sub": $(o).attr("sub"),
        }
        mounts.unshift(mount)
    })
    let cpu_req = $("#svc-res-cpu-req").val()
    let cpu_limit = $("#svc-res-cpu-limit").val()
    let mem_req = $("#svc-res-mem-req").val()
    let mem_limit = $("#svc-res-mem-limit").val()
    if (mem_req === "") {
        mem_req = "1Gi"
    }
    if (mem_limit === ""){
        mem_limit = "1Gi"
    }
    if(cpu_req === ""){
        cpu_req = "100m"
    }
    if (cpu_limit === ""){
        cpu_limit = "500m"
    }
    resources = {
        "reqCPU": cpu_req,
        "limitCPU": cpu_limit,
        "reqMemory":mem_req,
        "limitMemory": mem_limit
    }
    container["app"] = appName;
    container["image"] = image;
    container["ports"] = ports
    container["envVariables"] = envs
    container["mounts"] = mounts
    container["resources"] = resources
    return true
}