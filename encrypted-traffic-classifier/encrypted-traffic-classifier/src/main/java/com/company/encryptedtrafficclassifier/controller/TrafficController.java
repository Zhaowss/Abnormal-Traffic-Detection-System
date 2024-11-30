package com.company.encryptedtrafficclassifier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.encryptedtrafficclassifier.common.utils.SystemFilterBuildUtil;
import com.company.encryptedtrafficclassifier.entity.JsonResult;
import com.company.encryptedtrafficclassifier.entity.Traffic;
import com.company.encryptedtrafficclassifier.service.TrafficService;
import com.sun.jna.Platform;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/traffic")
@Api(tags = "流量信息")
public class TrafficController {

    @Autowired
    TrafficService trafficService;

    @GetMapping(value = "/list")
    @ApiOperation(value = "分页获取流量列表接口")
    public JsonResult list(
            @RequestParam(name = "page", required = false) Long page,
            @RequestParam(name = "limit", required = false) Long limit,
            @RequestParam(name = "protocol", required = false) String protocol,
            @RequestParam(name = "port", required = false) Long port,
            @RequestParam(name = "sourceIp", required = false) String sourceIp,
            @RequestParam(name = "destination_ip", required = false) String destination_ip,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(name = "startTime", required = false) LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            @RequestParam(name = "endTime", required = false) LocalDateTime endTime
    ) {
        if (null != page && null != limit) {
            QueryWrapper<Traffic> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(null != protocol && !"".equals(protocol), "protocol", protocol);
            queryWrapper.eq(null != port, "port", port);
            queryWrapper.like(null != sourceIp && !"".equals(sourceIp), "source_ip", sourceIp);
            queryWrapper.like(null != destination_ip && !"".equals(destination_ip), "destination_ip", destination_ip);
            queryWrapper.ge(null != startTime, "create_time", startTime);
            queryWrapper.le(null != endTime, "create_time", endTime);
            queryWrapper.orderByDesc("create_time");
            IPage<Traffic> trafficPage = trafficService.page(new Page<>(page, limit), queryWrapper);

            return JsonResult.ok("查询成功", trafficPage.getRecords(), trafficPage.getTotal());
        } else {
            return JsonResult.ok("查询成功", trafficService.list(), trafficService.count());
        }
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除流量接口")
    public JsonResult delete(@RequestBody JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("idList");
        List<Long> idList = JSONObject.parseArray(jsonArray.toJSONString(), Long.class);
        if (!trafficService.removeByIds(idList)) {
            return JsonResult.error("删除失败");
        }

        return JsonResult.ok("删除成功");
    }

    @PostMapping(value = "/gather")
    @ApiOperation(value = "流量捕获接口")
    public JsonResult gather(@RequestBody JSONObject jsonObject) throws PcapNativeException, NotOpenException, IOException, TimeoutException {
        String protocol = jsonObject.getString("protocol");
        Long port = jsonObject.getLong("port");
        String sourceIp = jsonObject.getString("sourceIp");
        String destinationIp = jsonObject.getString("destinationIp");
        Long timeLength = jsonObject.getLong("timeLength");
        if (null == timeLength) {
            timeLength = 10L;
        }

        String filter = SystemFilterBuildUtil.buildFilter(protocol, port, sourceIp, destinationIp);
        log.info("过滤规则：" + filter);
        int snapLen = 65536;
        PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
        int timeout = 10 * 1000;
        log.info("所有网卡设备：" + Pcaps.findAllDevs().toString());
        PcapNetworkInterface pcapNetworkInterface = Pcaps.findAllDevs().get(0);
        if (null == pcapNetworkInterface) {
            return JsonResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "没有网卡设备");
        }
        log.info("选中网卡设备：" + pcapNetworkInterface.getName() + "(" + pcapNetworkInterface.getDescription() + ")");

        PcapHandle handle = pcapNetworkInterface.openLive(snapLen, mode, timeout);
        handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        File fileDirectory = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "files");
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        String fileName = "traffic_" + System.currentTimeMillis() + ".pcap";
        String filePath = fileDirectory + File.separator + fileName;
        PcapDumper dumper = handle.dumpOpen(filePath);
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime >= timeLength * 1000) {
                log.info("采集结束");
                break;
            }
            Packet packet = handle.getNextPacket();
            if (null != packet) {
                dumper.dump(packet);
            }
        }
        PcapStat ps = handle.getStats();
        log.info("ps_recv: " + ps.getNumPacketsReceived());
        log.info("ps_drop: " + ps.getNumPacketsDropped());
        log.info("ps_ifdrop: " + ps.getNumPacketsDroppedByIf());
        if (Platform.isWindows()) {
            log.info("bs_capt: " + ps.getNumPacketsCaptured());
        }
        dumper.close();
        handle.close();

        File file = new File(filePath);
        Long fileSize = file.length();

        Traffic traffic = new Traffic();
        traffic.setProtocol(protocol);
        traffic.setPort(port);
        traffic.setSourceIp(sourceIp);
        traffic.setDestinationIp(destinationIp);
        traffic.setTimeLength(timeLength);
        traffic.setFileName(fileName);
        traffic.setFileSize(fileSize);
        traffic.setFilePath(filePath);
        if (!trafficService.save(traffic)) {
            return JsonResult.error("采集失败");
        }

        return JsonResult.ok("采集成功");
    }

    @GetMapping(value = "/count")
    @ApiOperation(value = "统计流量总数接口")
    public JsonResult count() {
        return JsonResult.ok("统计成功", trafficService.count());
    }

}