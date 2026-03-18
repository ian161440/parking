<template>
  <div class="layout" v-if="user">
    <header class="layout-header">
      <div class="layout-header-title">停车场管理系统 - 管理员</div>
      <div>
        <span style="margin-right: 16px;">当前管理员：{{ user.username }}</span>
        <a href="" @click.prevent="logout">退出登录</a>
      </div>
    </header>
    <div class="layout-content">
      <aside class="layout-sider">
        <ul>
          <li :class="{ active: activeMenu === 'profile' }" @click="activeMenu = 'profile'">个人中心</li>
          <li :class="{ active: activeMenu === 'vehicle' }" @click="activeMenu = 'vehicle'">车辆信息管理</li>
          <li :class="{ active: activeMenu === 'space' }" @click="activeMenu = 'space'">车位信息管理</li>
          <li :class="{ active: activeMenu === 'reservation' }" @click="activeMenu = 'reservation'">车位预定管理</li>
          <li :class="{ active: activeMenu === 'announcement' }" @click="activeMenu = 'announcement'">公告管理</li>
          <li :class="{ active: activeMenu === 'violation' }" @click="activeMenu = 'violation'">违规管理</li>
        </ul>
      </aside>
      <main class="layout-main">
        <!-- 个人中心 -->
        <section v-if="activeMenu === 'profile'">
          <h2 class="section-title">个人中心</h2>
          <p class="section-desc">展示和修改管理员的个人信息。</p>
          <div style="margin-top: 12px; padding: 12px; background-color: #ffffff; border-radius: 4px; border: 1px solid #f0f0f0;">
            <p>用户名：{{ user.username }}</p>
            <p>角色：管理员</p>
          </div>
        </section>

        <!-- 公告管理 -->
        <section v-else-if="activeMenu === 'announcement'">
          <h2 class="section-title">公告管理</h2>
          <p class="section-desc">通过本页面可以发布、编辑、删除及置顶公告。</p>
          <div v-if="announcementError" class="error">{{ announcementError }}</div>

          <div style="margin-bottom: 16px; padding: 12px; background-color: #ffffff; border-radius: 4px; border: 1px solid #f0f0f0;">
            <h3 style="margin-top: 0; font-size: 16px;">新建公告</h3>
            <form @submit.prevent="submitAnnouncement">
              <div class="form-item">
                <label>标题</label>
                <input v-model="announcementForm.title" />
              </div>
              <div class="form-item">
                <label>内容</label>
                <textarea v-model="announcementForm.content" rows="4" style="width: 100%; padding: 8px 10px; border-radius: 4px; border: 1px solid #d9d9d9;"></textarea>
              </div>
              <div class="form-item" style="display: flex; align-items: center;">
                <input id="isTopAdmin" type="checkbox" v-model="announcementForm.isTop" style="width: 16px; margin-right: 6px;" />
                <label for="isTopAdmin" style="margin: 0;">置顶</label>
              </div>
              <button type="submit" :disabled="announcementSubmitting">{{ announcementSubmitting ? '提交中...' : '发布公告' }}</button>
            </form>
          </div>

          <h3 style="font-size: 16px;">公告列表</h3>
          <p v-if="announcementLoading">公告加载中...</p>
          <p v-else-if="announcements.length === 0" style="font-size: 13px; color: #888;">暂无公告。</p>
          <table v-else style="width: 100%; border-collapse: collapse; font-size: 13px; background-color: #ffffff;">
            <thead>
              <tr style="background-color: #fafafa;">
                <th style="border: 1px solid #f0f0f0; padding: 6px;">ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">标题</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">是否置顶</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">发布时间</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="a in announcements" :key="a.id">
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ a.id }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ a.title }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ a.isTop ? '是' : '否' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ formatDateTime(a.createTime) }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">
                  <button type="button" style="width: auto; padding: 4px 10px; margin-right: 8px;" @click="startEditAnnouncement(a)">
                    编辑
                  </button>
                  <button type="button" style="width: auto; padding: 4px 10px; background-color: #ff4d4f;" @click="deleteAnnouncement(a.id)">
                    删除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </section>

        <!-- 车位信息管理 -->
        <section v-else-if="activeMenu === 'space'">
          <h2 class="section-title">车位信息管理</h2>
          <p class="section-desc">对车位进行新增、编辑、删除和状态调整。</p>

          <div v-if="spaceError" class="error">{{ spaceError }}</div>

          <div style="margin-bottom: 16px; padding: 12px; background-color: #ffffff; border-radius: 4px; border: 1px solid #f0f0f0;">
            <h3 style="margin-top: 0; font-size: 16px;">新建车位</h3>
            <form @submit.prevent="submitSpace">
              <div class="form-item">
                <label>车位编号</label>
                <input v-model="spaceForm.spaceCode" />
              </div>
              <div class="form-item">
                <label>楼层</label>
                <input v-model="spaceForm.floor" />
              </div>
              <div class="form-item">
                <label>类型</label>
                <input v-model="spaceForm.type" placeholder="例如：普通、新能源、无障碍" />
              </div>
              <div class="form-item">
                <label>状态</label>
                <select v-model="spaceForm.status" style="width: 100%; padding: 8px 10px; border-radius: 4px; border: 1px solid #d9d9d9;">
                  <option value="FREE">FREE（空闲）</option>
                  <option value="USING">USING（占用）</option>
                  <option value="REPAIR">REPAIR（维修）</option>
                </select>
              </div>
              <div class="form-item">
                <label>备注</label>
                <input v-model="spaceForm.remark" />
              </div>
              <button type="submit" :disabled="spaceSubmitting">{{ spaceSubmitting ? '提交中...' : (spaceForm.id ? '保存修改' : '新增车位') }}</button>
            </form>
          </div>

          <h3 style="font-size: 16px;">车位列表</h3>
          <p v-if="spaceLoading">车位信息加载中...</p>
          <p v-else-if="spaces.length === 0" style="font-size: 13px; color: #888;">暂无车位信息。</p>
          <table v-else style="width: 100%; border-collapse: collapse; font-size: 13px; background-color: #ffffff;">
            <thead>
              <tr style="background-color: #fafafa;">
                <th style="border: 1px solid #f0f0f0; padding: 6px;">ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车位编号</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">楼层</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">类型</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">状态</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">备注</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="s in spaces" :key="s.id">
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.id }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.spaceCode }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.floor }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.type || '普通' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.status }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ s.remark || '-' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">
                  <button type="button" style="width: auto; padding: 4px 10px; margin-right: 8px;" @click="editSpace(s)">
                    编辑
                  </button>
                  <button type="button" style="width: auto; padding: 4px 10px; background-color: #ff4d4f;" @click="deleteSpace(s.id)">
                    删除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </section>

        <!-- 车位预定管理 -->
        <section v-else-if="activeMenu === 'reservation'">
          <h2 class="section-title">车位预定管理</h2>
          <p class="section-desc">查看所有预约记录，并可以调整预约状态（如使用中、已完成、已取消等）。</p>

          <div v-if="reservationAdminError" class="error">{{ reservationAdminError }}</div>
          <p v-if="reservationAdminLoading">预约记录加载中...</p>
          <p v-else-if="allReservations.length === 0" style="font-size: 13px; color: #888;">暂无预约记录。</p>
          <table v-else style="width: 100%; border-collapse: collapse; font-size: 13px; background-color: #ffffff;">
            <thead>
              <tr style="background-color: #fafafa;">
                <th style="border: 1px solid #f0f0f0; padding: 6px;">ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">用户ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车辆ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车位ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">开始时间</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">结束时间</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">状态</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in allReservations" :key="r.id">
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.id }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.userId }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.vehicleId }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.spaceId }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ formatDateTime(r.startTime) }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ formatDateTime(r.endTime) }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ r.status }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">
                  <select v-model="r._newStatus" style="padding: 4px 6px; font-size: 12px; margin-right: 6px;">
                    <option disabled value="">选择新状态</option>
                    <option value="PENDING">PENDING</option>
                    <option value="USING">USING</option>
                    <option value="FINISHED">FINISHED</option>
                    <option value="CANCELLED">CANCELLED</option>
                  </select>
                  <button type="button" style="width: auto; padding: 4px 10px;" @click="updateReservationStatus(r)">
                    更新
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </section>

        <!-- 车辆信息管理（管理员查看所有车辆） -->
        <section v-else-if="activeMenu === 'vehicle'">
          <h2 class="section-title">车辆信息管理</h2>
          <p class="section-desc">查看系统中所有车辆信息，可用于统计与特殊车辆管理。</p>

          <div v-if="vehicleAdminError" class="error">{{ vehicleAdminError }}</div>
          <p v-if="vehicleAdminLoading">车辆信息加载中...</p>
          <p v-else-if="allVehicles.length === 0" style="font-size: 13px; color: #888;">暂无车辆信息。</p>
          <table v-else style="width: 100%; border-collapse: collapse; font-size: 13px; background-color: #ffffff;">
            <thead>
              <tr style="background-color: #fafafa;">
                <th style="border: 1px solid #f0f0f0; padding: 6px;">ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">用户ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车牌号</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车辆类型</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">是否特殊车辆</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">备注</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">创建时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="v in allVehicles" :key="v.id">
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.id }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.userId }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.plateNumber }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.vehicleType || '未指定' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.isSpecial ? '是' : '否' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.remark || '-' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ formatDateTime(v.createdAt) }}</td>
              </tr>
            </tbody>
          </table>
        </section>

        <!-- 违规管理 -->
        <section v-else-if="activeMenu === 'violation'">
          <h2 class="section-title">违规管理</h2>
          <p class="section-desc">录入新的违规记录，并修改违规处理状态。</p>

          <div v-if="violationError" class="error">{{ violationError }}</div>

          <div style="margin-bottom: 16px; padding: 12px; background-color: #ffffff; border-radius: 4px; border: 1px solid #f0f0f0;">
            <h3 style="margin-top: 0; font-size: 16px;">录入违规记录</h3>
            <form @submit.prevent="submitViolation">
              <div class="form-item">
                <label>用户ID</label>
                <input v-model.number="violationForm.userId" />
              </div>
              <div class="form-item">
                <label>车辆ID</label>
                <input v-model.number="violationForm.vehicleId" />
              </div>
              <div class="form-item">
                <label>车位ID</label>
                <input v-model.number="violationForm.spaceId" />
              </div>
              <div class="form-item">
                <label>违规描述</label>
                <textarea v-model="violationForm.description" rows="3" style="width: 100%; padding: 8px 10px; border-radius: 4px; border: 1px solid #d9d9d9;"></textarea>
              </div>
              <button type="submit" :disabled="violationSubmitting">{{ violationSubmitting ? '提交中...' : '提交违规记录' }}</button>
            </form>
          </div>

          <h3 style="font-size: 16px;">违规记录列表</h3>
          <p v-if="violationLoading">违规记录加载中...</p>
          <p v-else-if="violations.length === 0" style="font-size: 13px; color: #888;">暂无违规记录。</p>
          <table v-else style="width: 100%; border-collapse: collapse; font-size: 13px; background-color: #ffffff;">
            <thead>
              <tr style="background-color: #fafafa;">
                <th style="border: 1px solid #f0f0f0; padding: 6px;">ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">用户ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车辆ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">车位ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">描述</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">状态</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">处理人ID</th>
                <th style="border: 1px solid #f0f0f0; padding: 6px;">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="v in violations" :key="v.id">
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.id }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.userId }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.vehicleId }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.spaceId }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px; max-width: 260px; white-space: pre-wrap;">{{ v.description }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.status }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">{{ v.handlerId || '-' }}</td>
                <td style="border: 1px solid #f0f0f0; padding: 6px;">
                  <select v-model="v._newStatus" style="padding: 4px 6px; font-size: 12px; margin-right: 6px;">
                    <option disabled value="">选择状态</option>
                    <option value="UNPROCESSED">UNPROCESSED</option>
                    <option value="PROCESSING">PROCESSING</option>
                    <option value="FINISHED">FINISHED</option>
                  </select>
                  <button type="button" style="width: auto; padding: 4px 10px;" @click="updateViolationStatus(v)">
                    更新
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import http from '../api/http';

const router = useRouter();

const user = ref(null);
const activeMenu = ref('profile');

// 公告管理
const announcements = ref([]);
const announcementLoading = ref(false);
const announcementError = ref('');
const announcementSubmitting = ref(false);
const announcementForm = reactive({
  id: null,
  title: '',
  content: '',
  isTop: false
});

// 车位管理
const spaces = ref([]);
const spaceLoading = ref(false);
const spaceError = ref('');
const spaceSubmitting = ref(false);
const spaceForm = reactive({
  id: null,
  spaceCode: '',
  floor: '',
  type: '',
  status: 'FREE',
  remark: ''
});

// 预约管理
const allReservations = ref([]);
const reservationAdminLoading = ref(false);
const reservationAdminError = ref('');

// 车辆管理（管理员查看所有车辆）
const allVehicles = ref([]);
const vehicleAdminLoading = ref(false);
const vehicleAdminError = ref('');

// 违规管理
const violations = ref([]);
const violationLoading = ref(false);
const violationError = ref('');
const violationSubmitting = ref(false);
const violationForm = reactive({
  userId: null,
  vehicleId: null,
  spaceId: null,
  description: ''
});

const formatDateTime = (val) => {
  if (!val) return '';
  if (typeof val === 'string') {
    return val.replace('T', ' ').substring(0, 19);
  }
  return String(val);
};

// 公告
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

const resetAnnouncementForm = () => {
  announcementForm.id = null;
  announcementForm.title = '';
  announcementForm.content = '';
  announcementForm.isTop = false;
};

const submitAnnouncement = async () => {
  if (!announcementForm.title || !announcementForm.content) {
    announcementError.value = '标题和内容不能为空';
    return;
  }
  announcementSubmitting.value = true;
  announcementError.value = '';
  try {
    if (announcementForm.id) {
      // 更新
      const res = await http.put(`/api/announcements/admin/${announcementForm.id}`, announcementForm);
      if (!res.data || !res.data.success) {
        announcementError.value = res.data?.message || '更新公告失败';
      }
    } else {
      // 新增
      const payload = {
        title: announcementForm.title,
        content: announcementForm.content,
        isTop: announcementForm.isTop,
        publisherId: user.value.id
      };
      const res = await http.post('/api/announcements/admin', payload);
      if (!res.data || !res.data.success) {
        announcementError.value = res.data?.message || '发布公告失败';
      }
    }
    if (!announcementError.value) {
      await loadAnnouncements();
      resetAnnouncementForm();
    }
  } catch (e) {
    announcementError.value = e?.response?.data?.message || '保存公告失败';
  } finally {
    announcementSubmitting.value = false;
  }
};

const startEditAnnouncement = (a) => {
  announcementForm.id = a.id;
  announcementForm.title = a.title;
  announcementForm.content = a.content;
  announcementForm.isTop = !!a.isTop;
};

const deleteAnnouncement = async (id) => {
  if (!window.confirm('确认删除该公告吗？')) return;
  try {
    const res = await http.delete(`/api/announcements/admin/${id}`);
    if (res.data && res.data.success) {
      await loadAnnouncements();
    } else {
      announcementError.value = res.data?.message || '删除公告失败';
    }
  } catch (e) {
    announcementError.value = e?.response?.data?.message || '删除公告失败';
  }
};

// 车位
const loadSpaces = async () => {
  spaceLoading.value = true;
  spaceError.value = '';
  try {
    const res = await http.get('/api/spaces/admin/all');
    if (res.data && res.data.success) {
      spaces.value = res.data.data || [];
    } else {
      spaceError.value = res.data.message || '车位信息加载失败';
    }
  } catch (e) {
    spaceError.value = e?.response?.data?.message || '车位信息加载失败';
  } finally {
    spaceLoading.value = false;
  }
};

const resetSpaceForm = () => {
  spaceForm.id = null;
  spaceForm.spaceCode = '';
  spaceForm.floor = '';
  spaceForm.type = '';
  spaceForm.status = 'FREE';
  spaceForm.remark = '';
};

const submitSpace = async () => {
  if (!spaceForm.spaceCode) {
    spaceError.value = '车位编号不能为空';
    return;
  }
  spaceSubmitting.value = true;
  spaceError.value = '';
  try {
    if (spaceForm.id) {
      const res = await http.put(`/api/spaces/admin/${spaceForm.id}`, spaceForm);
      if (!res.data || !res.data.success) {
        spaceError.value = res.data?.message || '更新车位失败';
      }
    } else {
      const res = await http.post('/api/spaces/admin', spaceForm);
      if (!res.data || !res.data.success) {
        spaceError.value = res.data?.message || '新增车位失败';
      }
    }
    if (!spaceError.value) {
      await loadSpaces();
      resetSpaceForm();
    }
  } catch (e) {
    spaceError.value = e?.response?.data?.message || '保存车位失败';
  } finally {
    spaceSubmitting.value = false;
  }
};

const editSpace = (s) => {
  spaceForm.id = s.id;
  spaceForm.spaceCode = s.spaceCode;
  spaceForm.floor = s.floor;
  spaceForm.type = s.type;
  spaceForm.status = s.status;
  spaceForm.remark = s.remark;
};

const deleteSpace = async (id) => {
  if (!window.confirm('确认删除该车位吗？')) return;
  try {
    const res = await http.delete(`/api/spaces/admin/${id}`);
    if (res.data && res.data.success) {
      await loadSpaces();
    } else {
      spaceError.value = res.data?.message || '删除车位失败';
    }
  } catch (e) {
    spaceError.value = e?.response?.data?.message || '删除车位失败';
  }
};

// 预约
const loadAllReservations = async () => {
  reservationAdminLoading.value = true;
  reservationAdminError.value = '';
  try {
    const res = await http.get('/api/reservations/admin/all');
    if (res.data && res.data.success) {
      const list = res.data.data || [];
      list.forEach((r) => {
        if (!r._newStatus) r._newStatus = r.status || 'PENDING';
      });
      allReservations.value = list;
    } else {
      reservationAdminError.value = res.data.message || '预约记录加载失败';
    }
  } catch (e) {
    reservationAdminError.value = e?.response?.data?.message || '预约记录加载失败';
  } finally {
    reservationAdminLoading.value = false;
  }
};

const loadAllVehicles = async () => {
  vehicleAdminLoading.value = true;
  vehicleAdminError.value = '';
  try {
    const res = await http.get('/api/vehicles/admin/all');
    if (res.data && res.data.success) {
      allVehicles.value = res.data.data || [];
    } else {
      vehicleAdminError.value = res.data.message || '车辆信息加载失败';
    }
  } catch (e) {
    vehicleAdminError.value = e?.response?.data?.message || '车辆信息加载失败';
  } finally {
    vehicleAdminLoading.value = false;
  }
};

const updateReservationStatus = async (r) => {
  if (!r._newStatus) return;
  try {
    const res = await http.put(`/api/reservations/${r.id}/status`, null, { params: { status: r._newStatus } });
    if (res.data && res.data.success) {
      await loadAllReservations();
    } else {
      reservationAdminError.value = res.data?.message || '更新状态失败';
    }
  } catch (e) {
    reservationAdminError.value = e?.response?.data?.message || '更新状态失败';
  }
};

// 违规
const loadViolations = async () => {
  violationLoading.value = true;
  violationError.value = '';
  try {
    const res = await http.get('/api/violations/admin/all');
    if (res.data && res.data.success) {
      const list = res.data.data || [];
      list.forEach((v) => {
        if (!v._newStatus) v._newStatus = v.status || 'UNPROCESSED';
      });
      violations.value = list;
    } else {
      violationError.value = res.data.message || '违规记录加载失败';
    }
  } catch (e) {
    violationError.value = e?.response?.data?.message || '违规记录加载失败';
  } finally {
    violationLoading.value = false;
  }
};

const resetViolationForm = () => {
  violationForm.userId = null;
  violationForm.vehicleId = null;
  violationForm.spaceId = null;
  violationForm.description = '';
};

const submitViolation = async () => {
  if (!violationForm.description) {
    violationError.value = '违规描述不能为空';
    return;
  }
  violationSubmitting.value = true;
  violationError.value = '';
  try {
    const payload = {
      userId: violationForm.userId,
      vehicleId: violationForm.vehicleId,
      spaceId: violationForm.spaceId,
      description: violationForm.description,
      status: 'UNPROCESSED',
      handlerId: null
    };
    const res = await http.post('/api/violations/admin', payload);
    if (res.data && res.data.success) {
      await loadViolations();
      resetViolationForm();
    } else {
      violationError.value = res.data?.message || '提交违规记录失败';
    }
  } catch (e) {
    violationError.value = e?.response?.data?.message || '提交违规记录失败';
  } finally {
    violationSubmitting.value = false;
  }
};

const updateViolationStatus = async (v) => {
  if (!v._newStatus) return;
  try {
    const res = await http.put(`/api/violations/admin/${v.id}/status`, null, {
      params: {
        status: v._newStatus,
        handlerId: user.value.id
      }
    });
    if (res.data && res.data.success) {
      await loadViolations();
    } else {
      violationError.value = res.data?.message || '更新违规状态失败';
    }
  } catch (e) {
    violationError.value = e?.response?.data?.message || '更新违规状态失败';
  }
};

onMounted(async () => {
  const stored = localStorage.getItem('user');
  if (!stored) {
    router.push('/login');
    return;
  }
  const parsed = JSON.parse(stored);
  if (parsed.role !== 'ADMIN') {
    router.push('/user');
    return;
  }
  user.value = parsed;

  await loadAnnouncements();
  await loadSpaces();
  await loadAllReservations();
  await loadAllVehicles();
  await loadViolations();
});

const logout = () => {
  localStorage.removeItem('user');
  router.push('/login');
};
</script>
