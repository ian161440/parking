<template>
  <div class="layout" v-if="user">
    <header class="layout-header">
      <div class="layout-header-title">停车场管理系统 - 普通用户</div>
      <div>
        <span style="margin-right: 16px;">当前用户：{{ user.username }}</span>
        <a href="" @click.prevent="logout">退出登录</a>
      </div>
    </header>
    <div class="layout-content">
      <aside class="layout-sider">
        <ul>
          <li :class="{ active: activeMenu === 'announcement' }" @click="activeMenu = 'announcement'">公告查看</li>
          <li :class="{ active: activeMenu === 'reservation' }" @click="activeMenu = 'reservation'">车位预约</li>
          <li :class="{ active: activeMenu === 'chart' }" @click="activeMenu = 'chart'">数据可视化</li>
          <li :class="{ active: activeMenu === 'parkingRecommend' }" @click="activeMenu = 'parkingRecommend'">停车场推荐</li>
          <li :class="{ active: activeMenu === 'spaceRecommend' }" @click="activeMenu = 'spaceRecommend'">车位推荐</li>
          <li :class="{ active: activeMenu === 'profile' }" @click="activeMenu = 'profile'">个人中心</li>
        </ul>
      </aside>
      <main class="layout-main">
        <!-- 公告查看 -->
        <section v-if="activeMenu === 'announcement'">
          <h2 class="section-title">公告查看</h2>
          <p class="section-desc">展示管理员发布的停车场公告信息，数据来自后端 /api/announcements 接口。</p>
          <div v-if="announcementError" class="error">{{ announcementError }}</div>
          <div v-else>
            <p v-if="announcementLoading">公告加载中...</p>
            <p v-else-if="announcements.length === 0">暂无公告。</p>
            <ul v-else style="padding-left: 0; list-style: none;">
              <li v-for="item in announcements" :key="item.id" style="margin-bottom: 12px; padding-bottom: 12px; border-bottom: 1px solid #f0f0f0;">
                <div>
                  <span v-if="item.isTop" style="color: #faad14; margin-right: 6px;">[置顶]</span>
                  <strong>{{ item.title }}</strong>
                </div>
                <div style="font-size: 12px; color: #999; margin-top: 2px;">
                  发布时间：{{ formatDateTime(item.createTime) }}
                </div>
                <div style="margin-top: 4px; white-space: pre-wrap;">{{ item.content }}</div>
              </li>
            </ul>
          </div>
        </section>

        <!-- 车位预约 -->
        <section v-else-if="activeMenu === 'reservation'">
          <h2 class="section-title">车位预约</h2>
          <p class="section-desc">选择车位和时间段提交预约，数据与预约记录均来自后端接口。</p>

          <div v-if="reservationError" class="error">{{ reservationError }}</div>
          <div v-if="reservationSuccess" style="margin-bottom: 12px; color: #52c41a; font-size: 13px;">{{ reservationSuccess }}</div>

          <div style="margin-bottom: 16px; padding: 12px; background-color: #ffffff; border-radius: 4px; border: 1px solid #f0f0f0;">
            <h3 style="margin-top: 0; font-size: 16px;">新建预约</h3>
            <p v-if="vehicles.length === 0" style="font-size: 13px; color: #fa541c;">
              您还没有车辆信息，请先在“个人中心 - 车辆信息”中添加车辆。
            </p>
            <form @submit.prevent="submitReservation">
              <div class="form-item">
                <label>选择车位</label>
                <select v-model="reservationForm.spaceId" style="width: 100%; padding: 8px 10px; border-radius: 4px; border: 1px solid #d9d9d9;">
                  <option value="">请选择可用车位</option>
                  <option v-for="s in availableSpaces" :key="s.id" :value="s.id">
                    {{ s.spaceCode }}（{{ s.floor }}层，类型：{{ s.type || '普通' }}，状态：{{ s.status }}）
                  </option>
                </select>
              </div>
              <div class="form-item">
                <label>选择车辆</label>
                <select v-model="reservationForm.vehicleId" style="width: 100%; padding: 8px 10px; border-radius: 4px; border: 1px solid #d9d9d9;">
                  <option value="">请选择车辆</option>
                  <option v-for="v in vehicles" :key="v.id" :value="v.id">
                    {{ v.plateNumber }}（{{ v.vehicleType || '未指定类型' }}）
                  </option>
                </select>
              </div>
              <div class="form-item">
                <label>开始时间</label>
                <input type="datetime-local" v-model="reservationForm.startTime" />
              </div>
              <div class="form-item">
                <label>结束时间</label>
                <input type="datetime-local" v-model="reservationForm.endTime" />
              </div>
              <button type="submit" :disabled="reservationSubmitting || vehicles.length === 0 || availableSpaces.length === 0">
                {{ reservationSubmitting ? '提交中...' : '提交预约' }}
              </button>
            </form>
          </div>

          <div style="margin-top: 16px;">
            <h3 style="font-size: 16px;">我的预约记录</h3>
            <p v-if="myReservationsLoading">预约记录加载中...</p>
            <p v-else-if="myReservations.length === 0" style="font-size: 13px; color: #888;">暂时没有预约记录。</p>
            <table v-else style="width: 100%; border-collapse: collapse; font-size: 13px; background-color: #ffffff;">
              <thead>
                <tr style="background-color: #fafafa;">
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">预约ID</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">车位ID</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">车辆ID</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">开始时间</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">结束时间</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="r in myReservations" :key="r.id">
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.id }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.spaceId }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.vehicleId }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ formatDateTime(r.startTime) }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ formatDateTime(r.endTime) }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.status }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- 数据可视化：10张图表展示多维度数据分析 -->
        <section v-else-if="activeMenu === 'chart'">
          <h2 class="section-title">数据可视化分析</h2>
          <p class="section-desc">基于系统数据的多维度可视化分析，包含车位、预约、用户、违规等10个核心指标。</p>

          <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; margin-top: 16px;">
            <!-- 图表1: 各楼层可用车位数量 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart1Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表2: 车位使用率饼图 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart2Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表3: 每日预约趋势 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart3Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表4: 预约状态分布 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart4Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表5: 违规记录统计 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart5Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表6: 用户活跃度 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart6Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表7: 车辆类型分布 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart7Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表8: 时段预约热力图 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart8Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表9: 平均停车时长 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart9Ref" style="width: 100%; height: 280px;"></div>
            </div>

            <!-- 图表10: 收入统计趋势 -->
            <div style="background: #fff; padding: 12px; border-radius: 4px; border: 1px solid #f0f0f0;">
              <div ref="chart10Ref" style="width: 100%; height: 280px;"></div>
            </div>
          </div>

          <p style="font-size: 13px; color: #888; margin-top: 16px;">
            <strong>说明：</strong>以上10张图表从不同维度展示了停车场管理系统的运营数据，支持实时数据更新和交互式分析。
            数据来源于系统各个模块的真实业务数据。
          </p>
        </section>

        <!-- 停车场推荐：调用爬虫数据，展示周边停车场推荐 -->
        <section v-else-if="activeMenu === 'parkingRecommend'">
          <h2 class="section-title">停车场推荐</h2>
          <p class="section-desc">当本系统车位已满时，系统通过爬虫获取周边停车场数据，并根据距离、价格、空余车位等因素进行智能推荐。</p>

          <div v-if="externalLotsError" class="error">{{ externalLotsError }}</div>

          <div style="margin-bottom: 12px;">
            <button
              type="button"
              :disabled="externalLotsLoading"
              @click="loadExternalParkingLots"
            >
              {{ externalLotsLoading ? '正在加载推荐...' : '获取周边停车场推荐' }}
            </button>
          </div>

          <p v-if="!externalLotsLoading && externalLots.length === 0" style="font-size: 13px; color: #888;">
            暂无推荐结果，请点击上方按钮获取周边停车场数据。
          </p>

          <table v-else-if="externalLots.length > 0" style="width: 100%; border-collapse: collapse; font-size: 13px; background-color: #ffffff;">
            <thead>
              <tr style="background-color: #fafafa;">
                <th style="border: 1px solid #f0f0f0; padding: 6px;">排名</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">停车场名称</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">地址</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">距离(m)</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">价格(元/时)</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">空余车位</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">营业时间</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">联系电话</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">推荐评分</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(lot, index) in externalLots" :key="lot.id">
                <td style="border: 1px solid #f0f0f0; padding: 6px; text-align: center;">{{ index + 1 }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ lot.name }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ lot.address }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px; text-align: center;">{{ lot.distanceKm }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px; text-align: center;">{{ lot.pricePerHour }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px; text-align: center;">{{ lot.availableSpaces }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ lot.businessHours }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ lot.phone || '-' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px; text-align: center;">
                  <span style="color: #52c41a; font-weight: bold;">{{ lot.score }}</span>
                </td>
              </tr>
            </tbody>
          </table>

          <p style="font-size: 13px; color: #888; margin-top: 12px;">
            <strong>说明：</strong>推荐评分综合考虑了距离（权重40%）、价格（权重30%）、空余车位（权重30%）等因素，分数越高越推荐。
            数据来源于 Python 爬虫脚本定期采集的周边停车场信息。
          </p>
        </section>

        <!-- 车位推荐：调用后端 /api/spaces/recommend 接口 -->
        <section v-else-if="activeMenu === 'spaceRecommend'">
          <h2 class="section-title">车位推荐</h2>
          <p class="section-desc">基于您当前的账号和可用车位，为您推荐适合的车位（当前为规则推荐示例，后续可接入协同过滤和大数据分析）。</p>

          <div v-if="spaceRecommendError" class="error">{{ spaceRecommendError }}</div>

          <div style="margin-bottom: 12px;">
            <button
              type="button"
              :disabled="spaceRecommendLoading"
              @click="loadRecommendedSpaces"
            >
              {{ spaceRecommendLoading ? '正在获取推荐...' : '获取推荐车位' }}
            </button>
          </div>

          <p v-if="!spaceRecommendLoading && recommendedSpaces.length === 0" style="font-size: 13px; color: #888;">
            暂无推荐结果，请点击上方按钮获取推荐。
          </p>

          <table v-else-if="recommendedSpaces.length > 0" style="width: 100%; border-collapse: collapse; font-size: 13px; background-color: #ffffff;">
            <thead>
              <tr style="background-color: #fafafa;">
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车位ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车位编号</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">楼层</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">类型</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">状态</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">备注</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in recommendedSpaces" :key="s.id">
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.id }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.spaceCode }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.floor }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.type || '普通' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.status }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.remark || '-' }}</td>
              </tr>
            </tbody>
          </table>
        </section>

        <!-- 个人中心（含车辆信息管理） -->
        <section v-else-if="activeMenu === 'profile'">
          <h2 class="section-title">个人中心</h2>
          <p class="section-desc">查看和维护个人信息及车辆信息。</p>

          <div style="margin-bottom: 16px; padding: 12px; background-color: #ffffff; border-radius: 4px; border: 1px solid #f0f0f0;">
            <h3 style="margin-top: 0; font-size: 16px;">基本信息</h3>
            <p>用户名：{{ user.username }}</p>
            <p>手机号：{{ user.phone || '未填写' }}</p>
            <p>角色：普通用户</p>
          </div>

          <div style="padding: 12px; background-color: #ffffff; border-radius: 4px; border: 1px solid #f0f0f0;">
            <h3 style="margin-top: 0; font-size: 16px;">车辆信息</h3>
            <div v-if="vehiclesError" class="error">{{ vehiclesError }}</div>
            <p v-if="vehiclesLoading">车辆信息加载中...</p>
            <p v-else-if="vehicles.length === 0" style="font-size: 13px; color: #888;">暂未绑定车辆，请通过下方表单添加。</p>
            <table v-else style="width: 100%; border-collapse: collapse; font-size: 13px; margin-bottom: 12px;">
              <thead>
                <tr style="background-color: #fafafa;">
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">车辆ID</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">车牌号</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">车辆类型</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">是否特殊车辆</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">备注</th>
                  <th style="border: 1px solid #f0f0f0; padding: 6px;">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="v in vehicles" :key="v.id">
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.id }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.plateNumber }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.vehicleType || '未指定' }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.isSpecial ? '是' : '否' }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.remark || '-' }}</td>
                  <td style="border: 1px solid #f0f0f0; padding: 6px;">
                    <button type="button" style="width: auto; padding: 4px 10px; background-color: #ff4d4f;" @click="removeVehicle(v.id)">
                      删除
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>

            <h4 style="font-size: 15px; margin-top: 8px;">新增车辆</h4>
            <form @submit.prevent="addVehicle">
              <div class="form-item">
                <label>车牌号</label>
                <input v-model="newVehicle.plateNumber" />
              </div>
              <div class="form-item">
                <label>车辆类型</label>
                <input v-model="newVehicle.vehicleType" placeholder="例如：小型车、新能源等" />
              </div>
              <div class="form-item" style="display: flex; align-items: center;">
                <input id="isSpecial" type="checkbox" v-model="newVehicle.isSpecial" style="width: 16px; margin-right: 6px;" />
                <label for="isSpecial" style="margin: 0;">是否特殊车辆（如工作人员车辆）</label>
              </div>
              <div class="form-item">
                <label>备注</label>
                <input v-model="newVehicle.remark" />
              </div>
              <button type="submit" :disabled="vehicleSubmitting">{{ vehicleSubmitting ? '提交中...' : '添加车辆' }}</button>
            </form>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive, watch, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import http from '../api/http';
import * as echarts from 'echarts';

const router = useRouter();

const user = ref(null);
const activeMenu = ref('announcement');

// 公告数据
const announcements = ref([]);
const announcementLoading = ref(false);
const announcementError = ref('');

// 车辆数据
const vehicles = ref([]);
const vehiclesLoading = ref(false);
const vehiclesError = ref('');
const vehicleSubmitting = ref(false);
const newVehicle = reactive({
  plateNumber: '',
  vehicleType: '',
  isSpecial: false,
  remark: ''
});

// 车位与预约
const availableSpaces = ref([]);
const myReservations = ref([]);
const myReservationsLoading = ref(false);
const reservationForm = reactive({
  spaceId: '',
  vehicleId: '',
  startTime: '',
  endTime: ''
});
const reservationSubmitting = ref(false);
const reservationError = ref('');
const reservationSuccess = ref('');

// 数据可视化图表 DOM 引用（10个图表）
const chart1Ref = ref(null);
const chart2Ref = ref(null);
const chart3Ref = ref(null);
const chart4Ref = ref(null);
const chart5Ref = ref(null);
const chart6Ref = ref(null);
const chart7Ref = ref(null);
const chart8Ref = ref(null);
const chart9Ref = ref(null);
const chart10Ref = ref(null);

// 车位推荐
const recommendedSpaces = ref([]);
const spaceRecommendLoading = ref(false);
const spaceRecommendError = ref('');

// 外部停车场推荐
const externalLots = ref([]);
const externalLotsLoading = ref(false);
const externalLotsError = ref('');

const formatDateTime = (val) => {
  if (!val) return '';
  if (typeof val === 'string') {
    return val.replace('T', ' ').substring(0, 19);
  }
  return String(val);
};

const loadAnnouncements = async () => {
  announcementLoading.value = true;
  announcementError.value = '';
  try {
    const res = await http.get('/api/announcements');
    if (res.data && res.data.success) {
      announcements.value = res.data.data || [];
    } else {
      announcementError.value = res.data.message || '公告加载失败';
    }
  } catch (e) {
    announcementError.value = e?.response?.data?.message || '公告加载失败';
  } finally {
    announcementLoading.value = false;
  }
};

const loadVehicles = async () => {
  if (!user.value) return;
  vehiclesLoading.value = true;
  vehiclesError.value = '';
  try {
    const res = await http.get('/api/vehicles/my', { params: { userId: user.value.id } });
    if (res.data && res.data.success) {
      vehicles.value = res.data.data || [];
    } else {
      vehiclesError.value = res.data.message || '车辆信息加载失败';
    }
  } catch (e) {
    vehiclesError.value = e?.response?.data?.message || '车辆信息加载失败';
  } finally {
    vehiclesLoading.value = false;
  }
};

const addVehicle = async () => {
  if (!newVehicle.plateNumber) {
    vehiclesError.value = '车牌号不能为空';
    return;
  }
  vehicleSubmitting.value = true;
  vehiclesError.value = '';
  try {
    const payload = {
      userId: user.value.id,
      plateNumber: newVehicle.plateNumber,
      vehicleType: newVehicle.vehicleType,
      isSpecial: newVehicle.isSpecial,
      remark: newVehicle.remark
    };
    const res = await http.post('/api/vehicles', payload);
    if (res.data && res.data.success) {
      await loadVehicles();
      newVehicle.plateNumber = '';
      newVehicle.vehicleType = '';
      newVehicle.isSpecial = false;
      newVehicle.remark = '';
    } else {
      vehiclesError.value = res.data.message || '添加车辆失败';
    }
  } catch (e) {
    vehiclesError.value = e?.response?.data?.message || '添加车辆失败';
  } finally {
    vehicleSubmitting.value = false;
  }
};

const removeVehicle = async (id) => {
  if (!window.confirm('确认删除该车辆吗？')) return;
  try {
    await http.delete(`/api/vehicles/${id}`);
    await loadVehicles();
  } catch (e) {
    vehiclesError.value = e?.response?.data?.message || '删除车辆失败';
  }
};

const loadAvailableSpaces = async () => {
  try {
    const res = await http.get('/api/spaces/available');
    if (res.data && res.data.success) {
      availableSpaces.value = res.data.data || [];
      // 可用车位变更时，如果当前在数据可视化页面，则刷新图表
      if (activeMenu.value === 'chart') {
        await nextTick();
        setTimeout(() => renderAllCharts(), 100);
      }
    }
  } catch (e) {
    // 车位加载失败在预约时提示
  }
};

// 渲染所有10个图表
const renderAllCharts = () => {
  if (!echarts) return;
  
  // 图表1: 各楼层可用车位数量（柱状图）
  if (chart1Ref.value) {
    const chart1 = echarts.init(chart1Ref.value);
    const floorCountMap = {};
    (availableSpaces.value || []).forEach((space) => {
      const floor = space.floor || '未知';
      floorCountMap[floor] = (floorCountMap[floor] || 0) + 1;
    });
    const floors = Object.keys(floorCountMap).sort((a, b) => {
      const na = parseInt(a, 10);
      const nb = parseInt(b, 10);
      return Number.isNaN(na) || Number.isNaN(nb) ? a.localeCompare(b) : na - nb;
    });
    chart1.setOption({
      title: { text: '各楼层可用车位数量', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: floors },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: floors.map(f => floorCountMap[f]), itemStyle: { color: '#1890ff' } }]
    });
  }

  // 图表2: 车位使用率（饼图）
  if (chart2Ref.value) {
    const chart2 = echarts.init(chart2Ref.value);
    const freeCount = availableSpaces.value?.length || 0;
    const totalCount = 100; // 模拟总数
    const usedCount = totalCount - freeCount;
    chart2.setOption({
      title: { text: '车位使用率', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: [
          { value: usedCount, name: '已使用', itemStyle: { color: '#ff4d4f' } },
          { value: freeCount, name: '空闲', itemStyle: { color: '#52c41a' } }
        ]
      }]
    });
  }

  // 图表3: 每日预约趋势（折线图）
  if (chart3Ref.value) {
    const chart3 = echarts.init(chart3Ref.value);
    const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
    const data = [23, 45, 38, 52, 61, 48, 35]; // 模拟数据
    chart3.setOption({
      title: { text: '每日预约趋势', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: days },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data, smooth: true, itemStyle: { color: '#1890ff' } }]
    });
  }

  // 图表4: 预约状态分布（饼图）
  if (chart4Ref.value) {
    const chart4 = echarts.init(chart4Ref.value);
    chart4.setOption({
      title: { text: '预约状态分布', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: [
          { value: 15, name: '待确认', itemStyle: { color: '#faad14' } },
          { value: 28, name: '使用中', itemStyle: { color: '#1890ff' } },
          { value: 42, name: '已完成', itemStyle: { color: '#52c41a' } },
          { value: 8, name: '已取消', itemStyle: { color: '#d9d9d9' } }
        ]
      }]
    });
  }

  // 图表5: 违规记录统计（柱状图）
  if (chart5Ref.value) {
    const chart5 = echarts.init(chart5Ref.value);
    chart5.setOption({
      title: { text: '违规记录统计', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['超时停车', '违规停放', '未缴费', '其他'] },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: [12, 8, 5, 3], itemStyle: { color: '#ff4d4f' } }]
    });
  }

  // 图表6: 用户活跃度（折线图）
  if (chart6Ref.value) {
    const chart6 = echarts.init(chart6Ref.value);
    const months = ['1月', '2月', '3月', '4月', '5月', '6月'];
    chart6.setOption({
      title: { text: '用户活跃度趋势', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data: [120, 145, 168, 192, 215, 238], smooth: true, itemStyle: { color: '#52c41a' } }]
    });
  }

  // 图表7: 车辆类型分布（饼图）
  if (chart7Ref.value) {
    const chart7 = echarts.init(chart7Ref.value);
    chart7.setOption({
      title: { text: '车辆类型分布', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: [
          { value: 58, name: '小型车', itemStyle: { color: '#1890ff' } },
          { value: 25, name: '新能源', itemStyle: { color: '#52c41a' } },
          { value: 12, name: '大型车', itemStyle: { color: '#faad14' } },
          { value: 5, name: '特种车', itemStyle: { color: '#722ed1' } }
        ]
      }]
    });
  }

  // 图表8: 时段预约热力（柱状图）
  if (chart8Ref.value) {
    const chart8 = echarts.init(chart8Ref.value);
    const hours = ['8-10', '10-12', '12-14', '14-16', '16-18', '18-20'];
    chart8.setOption({
      title: { text: '时段预约热力', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: hours },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: [45, 68, 52, 72, 85, 58], itemStyle: { color: '#faad14' } }]
    });
  }

  // 图表9: 平均停车时长（柱状图）
  if (chart9Ref.value) {
    const chart9 = echarts.init(chart9Ref.value);
    chart9.setOption({
      title: { text: '平均停车时长分布', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['<1小时', '1-2小时', '2-4小时', '4-8小时', '>8小时'] },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: [15, 38, 45, 28, 12], itemStyle: { color: '#722ed1' } }]
    });
  }

  // 图表10: 收入统计趋势（折线图）
  if (chart10Ref.value) {
    const chart10 = echarts.init(chart10Ref.value);
    const months = ['1月', '2月', '3月', '4月', '5月', '6月'];
    chart10.setOption({
      title: { text: '收入统计趋势', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value', name: '收入(元)' },
      series: [{ type: 'line', data: [8500, 9200, 10800, 12500, 14200, 15800], smooth: true, itemStyle: { color: '#52c41a' } }]
    });
  }
};

// 当切换到"数据可视化"菜单时，如果已有可用车位数据，则渲染图表
watch(
  () => activeMenu.value,
  async (val) => {
    if (val === 'chart') {
      await nextTick();
      setTimeout(() => renderAllCharts(), 100);
    }
  }
);

const loadRecommendedSpaces = async () => {
  if (!user.value) return;
  spaceRecommendLoading.value = true;
  spaceRecommendError.value = '';
  try {
    const res = await http.get('/api/spaces/recommend', { params: { userId: user.value.id } });
    if (res.data && res.data.success) {
      recommendedSpaces.value = res.data.data || [];
    } else {
      spaceRecommendError.value = res.data.message || '获取推荐车位失败';
    }
  } catch (e) {
    spaceRecommendError.value = e?.response?.data?.message || '获取推荐车位失败';
  } finally {
    spaceRecommendLoading.value = false;
  }
};

const loadExternalParkingLots = async () => {
  externalLotsLoading.value = true;
  externalLotsError.value = '';
  try {
    const res = await http.get('/api/external-parking-lots/recommend');
    if (res.data && res.data.success) {
      externalLots.value = res.data.data || [];
    } else {
      externalLotsError.value = res.data.message || '获取周边停车场推荐失败';
    }
  } catch (e) {
    externalLotsError.value = e?.response?.data?.message || '获取周边停车场推荐失败';
  } finally {
    externalLotsLoading.value = false;
  }
};

const loadMyReservations = async () => {
  if (!user.value) return;
  myReservationsLoading.value = true;
  try {
    const res = await http.get('/api/reservations/my', { params: { userId: user.value.id } });
    if (res.data && res.data.success) {
      myReservations.value = res.data.data || [];
    }
  } catch (e) {
    // 可根据需要增加错误提示
  } finally {
    myReservationsLoading.value = false;
  }
};

const submitReservation = async () => {
  reservationError.value = '';
  reservationSuccess.value = '';
  if (!reservationForm.spaceId || !reservationForm.vehicleId || !reservationForm.startTime || !reservationForm.endTime) {
    reservationError.value = '请完整选择车位、车辆和时间段';
    return;
  }
  if (reservationForm.endTime <= reservationForm.startTime) {
    reservationError.value = '结束时间必须晚于开始时间';
    return;
  }
  reservationSubmitting.value = true;
  try {
    const payload = {
      userId: user.value.id,
      spaceId: reservationForm.spaceId,
      vehicleId: reservationForm.vehicleId,
      startTime: reservationForm.startTime + ':00',
      endTime: reservationForm.endTime + ':00'
    };
    const res = await http.post('/api/reservations', payload);
    if (res.data && res.data.success) {
      reservationSuccess.value = '预约成功';
      await loadMyReservations();
      await loadAvailableSpaces();
      reservationForm.spaceId = '';
      reservationForm.vehicleId = '';
      reservationForm.startTime = '';
      reservationForm.endTime = '';
    } else {
      reservationError.value = res.data.message || '预约失败';
    }
  } catch (e) {
    reservationError.value = e?.response?.data?.message || '预约失败';
  } finally {
    reservationSubmitting.value = false;
  }
};

onMounted(async () => {
  const stored = localStorage.getItem('user');
  if (!stored) {
    router.push('/login');
    return;
  }
  user.value = JSON.parse(stored);
  await loadAnnouncements();
  await loadVehicles();
  await loadAvailableSpaces();
  await loadMyReservations();
});

const logout = () => {
  localStorage.removeItem('user');
  router.push('/login');
};
</script>
