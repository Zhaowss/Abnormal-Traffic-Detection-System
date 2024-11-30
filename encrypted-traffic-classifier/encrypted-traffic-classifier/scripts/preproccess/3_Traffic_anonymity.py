import os
import sys
from scapy.all import rdpcap, wrpcap
from scapy.layers.inet import IP
from scapy.layers.l2 import Ether
from pathlib import Path

# 增加递归深度限制
sys.setrecursionlimit(3000)


def anonymize_packet(packet):
    try:
        # 匿名化IP层
        if packet.haslayer(IP):
            packet[IP].src = "0.0.0.0"
            packet[IP].dst = "0.0.0.0"
        # 匿名化MAC地址（假设以太网层存在）
        if packet.haslayer(Ether):
            packet[Ether].src = "00:00:00:00:00:00"
            packet[Ether].dst = "00:00:00:00:00:00"
        return packet
    except Exception as e:
        print(f"Error anonymizing packet: {e}")
        return None


# 路径
rootdirs = [
    Path(__file__).parent / Path('3_ProcessedSession') / Path('FilteredSession') / Path('Train'),
    Path(__file__).parent / Path('3_ProcessedSession') / Path('FilteredSession') / Path('Test')
]
save_paths = [
    Path(__file__).parent / Path('3_ProcessedSession') / Path('Anonymity') / Path('Train'),
    Path(__file__).parent / Path('3_ProcessedSession') / Path('Anonymity') / Path('Test')
]
for (rootdir, save_path) in zip(rootdirs, save_paths):
    # 遍历文件夹
    for root, dirs, files in os.walk(rootdir):
        for dire in dirs:
            print(f"处理目录: {dire}")
            # 创建匿名化文件保存目录
            new_dir = os.path.join(save_path, dire)
            os.makedirs(new_dir, exist_ok=True)

            # 处理目录中的每个pcap文件
            for f in os.listdir(os.path.join(root, dire)):
                f_path = os.path.join(root, dire, f)
                d_path = os.path.join(new_dir, f)
                try:
                    # 读取pcap文件
                    packets = rdpcap(f_path)
                    # 匿名化数据包
                    anonymized_packets = [anonymize_packet(packet) for packet in packets if
                                          anonymize_packet(packet) is not None]
                    # 将匿名化后的数据包写入新的pcap文件
                    wrpcap(d_path, anonymized_packets)
                except Exception as e:
                    print(f"处理文件 {f} 时出错: {e}")
